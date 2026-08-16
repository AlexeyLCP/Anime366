package su.afk.yummy.tv.data.player.extractor.alloha

/** Name the WebView's `@JavascriptInterface` bridge is registered under - referenced by the JS below. */
internal const val BRIDGE_NAME = "AndroidBridge"

/**
 * HTML/JS shell that wraps the Alloha iframe and observes its own network stack (XHR/fetch/
 * WebSocket) to capture the signed HLS session - see [BRIDGE_NAME] for the Kotlin-side bridge this
 * talks to.
 */
internal fun wrapperHtml(iframeUrl: String): String = """
    <html><body style="margin:0;background:black">
    <iframe id="alloha" src="${iframeUrl.escapeHtml()}" width="100%" height="100%" frameborder="0" allowfullscreen></iframe>
    <script>
    try {
      Object.defineProperty(document, 'visibilityState', {get:function(){return 'visible'}});
      Object.defineProperty(document, 'hidden', {get:function(){return false}});
    } catch(e) {}
    // Shared across installs: the iframe hands us a fresh window when it navigates from its
    // initial about:blank to the real player URL, and the state has to survive that.
    var bnsi = null, headers = {}, done = false;
    function allohaInstall(frame) {
      var w, href, xhrProtoOpen;
      try { w = frame.contentWindow; } catch(e) { return false; }
      if(!w) return false;
      // Not ready (or not same-origin yet) - try again on the next poll.
      try { href = w.location.href; } catch(e) { return false; }
      if(!href || href === 'about:blank') return false;
      // The guard lives on the wrapper function itself, not on the window: a window expando did
      // not hold and the poll below re-wrapped the hooks hundreds of times, each wrapping the
      // previous one. A fresh document always brings a pristine native open(), so this both
      // prevents double-wrapping and still re-installs after navigation.
      try { xhrProtoOpen = w.XMLHttpRequest.prototype.open; } catch(e) { return false; }
      if(!xhrProtoOpen) return false;
      if(xhrProtoOpen.__alloha) return true;
      try {
        try {
          Object.defineProperty(w.document, 'visibilityState', {get:function(){return 'visible'}});
          Object.defineProperty(w.document, 'hidden', {get:function(){return false}});
        } catch(e) {}
        var pushTimer = null;
        function put(k,v) {
          if(!k || !v) return;
          headers[String(k).toLowerCase()] = String(v);
          if(done) {
            if(pushTimer) clearTimeout(pushTimer);
            pushTimer = setTimeout(function(){ AndroidBridge.onStreamHeaders(JSON.stringify(headers)); }, 40);
          }
        }
        function ready() {
          if(done || !bnsi || !headers['authorizations'] || !headers['accepts-controls']) return;
          done = true;
          AndroidBridge.onReady(bnsi, JSON.stringify(headers));
        }
        put('origin', w.location.origin); put('referer', w.location.origin + '/');
        put('user-agent', w.navigator.userAgent); put('accept', '*/*');
        put('sec-fetch-dest', 'empty'); put('sec-fetch-mode', 'cors'); put('sec-fetch-site', 'cross-site');

        var lastMasterUrl = null;
        // Hand the proxy the correctly-signed master.m3u8 the player itself requests, even when
        // the browser blocks that request. The player fetches master with custom headers
        // (accepts-controls/authorizations) -> CORS preflight the CDN never answers -> the
        // request is blocked and 'load'/'ok' never fire, so only the requested URL is available.
        // Our server-side proxy isn't subject to CORS, so this up-to-date URL streams fine,
        // unlike the stale bnsi URL that the CDN 403s with token_decrypt.
        function isCdnMaster(url) {
          return !!url && url.indexOf('http') === 0 && url.indexOf('master.m3u8') !== -1;
        }
        function reportMaster(url) {
          if(!done || !url) return;
          if(!isCdnMaster(url)) return;
          if(url === lastMasterUrl) return;
          lastMasterUrl = url;
          AndroidBridge.onM3u8Refreshed(url, JSON.stringify(headers));
        }
        var primaryHost = null, fallbackHost = null, fallbackMasterUrl = null;
        function extractCdnHosts() {
          if(primaryHost || !bnsi) return;
          try {
            var data = JSON.parse(bnsi), sources = data.hlsSource;
            if(!sources || !sources[0] || !sources[0].quality) return;
            var quality = sources[0].quality, key = Object.keys(quality)[0];
            var urls = quality[key].split(' or ');
            if(urls.length < 2) return;
            var primary = urls[0].match(/https?:\/\/([^\/]+)/);
            var fallback = urls[1].trim().match(/https?:\/\/([^\/]+)/);
            if(primary) primaryHost = primary[1];
            if(fallback) {
              fallbackHost = fallback[1];
              fallbackMasterUrl = urls[1].trim();
            }
            AndroidBridge.onLog('CDN candidates captured');
          } catch(e) { AndroidBridge.onLog('CDN candidate parse failed'); }
        }

        var open = w.XMLHttpRequest.prototype.open;
        w.XMLHttpRequest.prototype.open = function(method,url) {
          this.__allohaUrl = url;
          this.addEventListener('load', function() {
            var url = this.responseURL || this.__allohaUrl || '';
            if(url.indexOf('/bnsi/') !== -1) {
              bnsi = this.responseText;
              extractCdnHosts();
              ready();
            }
            reportMaster(url);
          });
          // Fallback capture: loadend fires on success and on failure. The send() hook below is
          // the primary capture point (and blocks CDN-master requests); this only covers any
          // request that reaches loadend without having passed through our send() override.
          this.addEventListener('loadend', function() {
            reportMaster(this.__allohaUrl || this.responseURL || '');
          });
          return open.apply(this, arguments);
        };
        w.XMLHttpRequest.prototype.open.__alloha = true;
        var setHeader = w.XMLHttpRequest.prototype.setRequestHeader;
        w.XMLHttpRequest.prototype.setRequestHeader = function(k,v) {
          put(k,v); ready(); return setHeader.apply(this, arguments);
        };
        // The player's own master.m3u8 request is deliberately left alone. It used to be withheld
        // here on the premise that the CDN path token is single-use and whoever GETs it first
        // burns it - but alloha-parser-kotlin only observes the request and then re-fetches the
        // very same URL through its own proxy, and streams fine. Withholding it instead left the
        // page's player with no media at all (readyState 0), so it tore down its WebSocket after
        // ~2.5s, no config_update ever arrived, and every session rotation stalled on a signal
        // that could not come. Master capture now rides the load/loadend listeners above.
        var fetch = w.fetch;
        w.fetch = function(input,init) {
          try {
            var url = typeof input === 'string' ? input : (input && input.url ? input.url : '');
            if(init && init.headers) {
              if(typeof init.headers.forEach === 'function') init.headers.forEach(function(v,k){put(k,v)});
              else for(var k in init.headers) put(k,init.headers[k]);
            }
            ready();
            extractCdnHosts();
            reportMaster(url);
            if(url && (url.indexOf('.m3u8') !== -1 || url.indexOf('.ts') !== -1 || url.indexOf('.m4s') !== -1) &&
                primaryHost && fallbackHost && url.indexOf(primaryHost) !== -1) {
              var fallbackUrl = url.indexOf('master.m3u8') !== -1 && fallbackMasterUrl
                ? fallbackMasterUrl : url.replace(primaryHost, fallbackHost);
              return fetch.apply(this, arguments).then(function(response) {
                if(response.status !== 403 && response.status !== 500 && response.status !== 503) return response;
                AndroidBridge.onLog('Browser CDN fallback after status=' + response.status);
                return fetch.call(w, fallbackUrl, init).then(function(fallbackResponse) {
                  if(fallbackResponse.ok) reportMaster(fallbackUrl);
                  return fallbackResponse;
                });
              });
            }
          } catch(e) {}
          return fetch.apply(this, arguments);
        };

        var OrigWS = w.WebSocket, send = OrigWS.prototype.send;
        var heartbeat = null, started = Date.now(), activeSocket = null, lastEdgeHash = null;
        function startHeartbeat(socket) {
          if(heartbeat) clearInterval(heartbeat);
          started = Date.now();
          heartbeat = setInterval(function() {
            if(!done || !socket || socket.readyState !== 1) return;
            try {
              send.call(socket, JSON.stringify({type:'playing',current_time:Math.floor((Date.now()-started)/1000),resolution:'1080',track_id:'1',speed:1,subtitle:0,ts:Date.now()}));
              AndroidBridge.onLog('heartbeat sent');
            } catch(e) { AndroidBridge.onLog('heartbeat failed'); }
          }, 25000);
        }
        function hookSocket(socket) {
          if(!socket || socket.__allohaHooked) return socket;
          socket.__allohaHooked = true;
          activeSocket = socket;
          started = Date.now();
            AndroidBridge.onLog('WebSocket hooked');
          socket.addEventListener('message', function(event) {
            try {
              var message = JSON.parse(event.data);
              if(message && message.type === 'config_update' && message.edge_hash && message.edge_hash !== lastEdgeHash) {
                lastEdgeHash = message.edge_hash;
                put('accepts-controls', message.edge_hash); ready();
                var ttl = message.ttl || 120;
                AndroidBridge.onLog('config_update ttl=' + ttl);
                AndroidBridge.onConfigUpdate(message.edge_hash, ttl, JSON.stringify(headers));
              }
            } catch(e) {}
          });
          socket.addEventListener('open', function() {
            AndroidBridge.onLog('WebSocket opened');
            startHeartbeat(socket);
          });
          socket.addEventListener('close', function(event){
            if(activeSocket === socket) {
              activeSocket = null;
              if(heartbeat) clearInterval(heartbeat);
            }
            var reason = event && event.reason ? String(event.reason).replace(/\s+/g, ' ').slice(0, 80) : '';
            AndroidBridge.onLog('WebSocket closed code=' + (event ? event.code : 0) +
              ' clean=' + (event ? event.wasClean : false) + ' reason=' + reason);
          });
          if(socket.readyState === 1) startHeartbeat(socket);
          return socket;
        }
        OrigWS.prototype.send = function(data) {
          hookSocket(this);
          return send.call(this,data);
        };
        w.WebSocket = function(url, protocols) {
          return hookSocket(protocols ? new OrigWS(url, protocols) : new OrigWS(url));
        };
        w.WebSocket.prototype = OrigWS.prototype;
        w.WebSocket.CONNECTING = OrigWS.CONNECTING;
        w.WebSocket.OPEN = OrigWS.OPEN;
        w.WebSocket.CLOSING = OrigWS.CLOSING;
        w.WebSocket.CLOSED = OrigWS.CLOSED;
        var errorReported = false;
        var unavailablePattern = /озвучка\s*недоступна/i;
        setInterval(function() {
          if(!done && !errorReported) {
            try {
              var text = w.document.body ? w.document.body.textContent : '';
              if(text && unavailablePattern.test(text)) {
                errorReported = true;
                AndroidBridge.onDubbingUnavailable();
                return;
              }
            } catch(e) {}
          }
          // Keep the iframe player trying to play even AFTER the session is captured, so it keeps
          // (re)fetching its own correctly-signed master.m3u8 (onM3u8Refreshed) instead of settling
          // on the raw bnsi URL that the CDN rejects with 403 token_decrypt.
          //
          // Note it never actually plays: the send()/fetch hooks above withhold its master request
          // to keep that single-use token fresh for our proxy, so its media element stays at
          // readyState 0 with currentTime pinned at 0 (measured on device). The player therefore
          // tears its own WebSocket down ~2.5s after opening it and no config_update ever arrives -
          // that is inherent to withholding the master, not a bug to chase. LiveAllohaStreamSession
          // detects it via sawConfigUpdate and stops waiting on signals that cannot come.
          // The play control is only pressed while the player is NOT already playing. Verified
          // against the live markup: the root carries `allplay--playing` while it runs, and the
          // toggle button reports aria-label "Пауза" in that state - clicking it then would stop
          // playback, which is the opposite of what this loop is for. (The previous selector,
          // `.allplay__play-btn`, matched nothing at all on the current player, so this click had
          // silently been a no-op.)
          var root = w.document.querySelector('.allplay');
          if(!root || !root.classList.contains('allplay--playing')) {
            var button = w.document.querySelector('button.allplay__controls__item.allplay__control');
            if(button) button.click();
          }
          var video = w.document.querySelector('video');
          if(video) { video.muted = true; if(video.paused) video.play().catch(function(){}); }
        }, 1500);
        return true;
      } catch(e) { AndroidBridge.onLog('wrapper install failed: ' + e); return false; }
    }
    // Installed as soon as the iframe's document is reachable, NOT on its 'load' event. By the
    // time 'load' fires the page's own scripts have run and its /bnsi/ request may already be
    // complete, so the XHR hook never sees it and the extraction sits out the whole timeout while
    // the player itself plays perfectly (measured on device: video readyState 4, player DOM
    // complete, zero bnsi observed). The poll is bounded; onload stays as a safety net and also
    // covers any later navigation of the frame.
    var allohaFrame = document.getElementById('alloha');
    allohaFrame.onload = function() { allohaInstall(allohaFrame); };
    var installPoll = setInterval(function() {
      if(allohaInstall(allohaFrame)) clearInterval(installPoll);
    }, 20);
    setTimeout(function() { clearInterval(installPoll); }, 8000);
    allohaInstall(allohaFrame);
    </script></body></html>
""".trimIndent()

private fun String.escapeHtml(): String = replace("&", "&amp;").replace("\"", "&quot;")
