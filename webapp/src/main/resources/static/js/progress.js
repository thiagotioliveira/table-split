/**
 * TableSplit - Slim Progress Bar
 * 
 * Intercepta requisições AJAX e mudanças de página para mostrar feedback visual elegante.
 */
(function() {
    'use strict';

    const ProgressBar = {
        element: null,
        progress: 0,
        timer: null,
        activeRequests: 0,

        init() {
            if (this.element) return;
            this.element = document.createElement('div');
            this.element.className = 'slim-progress-bar';
            document.body.appendChild(this.element);
        },

        start() {
            this.activeRequests++;
            if (this.activeRequests > 1) return;

            this.init();
            
            // Garantir que a barra resete antes de começar se já estiver oculta
            if (parseFloat(this.element.style.opacity || '1') === 0) {
                this.element.style.transition = 'none';
                this.element.style.width = '0%';
                this.element.style.opacity = '1';
                // Force reflow
                this.element.offsetHeight;
                this.element.style.transition = '';
            }

            this.progress = 5; // Começa com um pulo inicial
            this.element.style.width = this.progress + '%';
            this.element.style.opacity = '1';
            this.element.style.display = 'block';
            
            clearInterval(this.timer);
            this.timer = setInterval(() => {
                // Efeito trickle: avança progressivamente mais devagar
                if (this.progress < 70) {
                    this.progress += Math.random() * 3 + 1;
                } else if (this.progress < 90) {
                    this.progress += Math.random() * 1;
                } else if (this.progress < 98) {
                    this.progress += 0.1;
                }
                
                this.element.style.width = this.progress + '%';
            }, 300);
        },

        finish() {
            this.activeRequests = Math.max(0, this.activeRequests - 1);
            if (this.activeRequests > 0) return;

            clearInterval(this.timer);
            if (!this.element) return;

            this.element.style.width = '100%';
            
            setTimeout(() => {
                this.element.style.opacity = '0';
                setTimeout(() => {
                    if (this.activeRequests === 0) {
                        this.element.style.display = 'none';
                        this.element.style.width = '0%';
                    }
                }, 400);
            }, 200);
        }
    };

    // --- Interceptadores ---

    // 1. Interceptar XMLHttpRequest
    const XHR = XMLHttpRequest.prototype;
    const send = XHR.send;
    const open = XHR.open;

    XHR.open = function(method, url) {
        this._url = url;
        return open.apply(this, arguments);
    };

    XHR.send = function() {
        // Ignorar requisições curtas ou de polling se necessário
        const isPolling = this._url && (this._url.includes('sse') || this._url.includes('count/pending'));
        
        if (!isPolling) {
            ProgressBar.start();
            this.addEventListener('loadend', () => ProgressBar.finish());
        }
        
        return send.apply(this, arguments);
    };

    // 2. Interceptar Fetch
    const originalFetch = window.fetch;
    window.fetch = function(input, init) {
        const url = typeof input === 'string' ? input : (input instanceof Request ? input.url : '');
        const isPolling = url.includes('sse') || url.includes('count/pending') || url.includes('count');

        if (!isPolling) {
            ProgressBar.start();
            return originalFetch.apply(this, arguments).finally(() => {
                ProgressBar.finish();
            });
        }
        return originalFetch.apply(this, arguments);
    };

    // 3. Navegação entre páginas (cliques em links)
    document.addEventListener('click', (e) => {
        const link = e.target.closest('a');
        if (link && 
            link.href && 
            !link.href.startsWith('javascript:') && 
            !link.href.startsWith('#') &&
            link.target !== '_blank' &&
            !e.ctrlKey && !e.shiftKey && !e.metaKey && !e.altKey &&
            link.hostname === window.location.hostname &&
            link.getAttribute('download') === null) {
            
            ProgressBar.start();
        }
    });

    // 4. Submissão de formulários
    document.addEventListener('submit', (e) => {
        const form = e.target;
        if (form.getAttribute('target') !== '_blank') {
            ProgressBar.start();
        }
    });

    // Adicionar listener para quando a página carrega completamente (útil se o script carregar no meio)
    if (document.readyState === 'loading') {
        ProgressBar.start();
        window.addEventListener('load', () => ProgressBar.finish());
    }

    // Expor globalmente
    window.TableSplitProgress = ProgressBar;

})();
