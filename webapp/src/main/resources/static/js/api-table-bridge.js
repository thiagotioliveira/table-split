/**
 * tablesApi Bridge
 *
 * AUTO-GENERATED — do NOT edit manually.
 * Source: table-v1.yaml
 * Regenerated on every Maven build.
 *
 * Uses native browser fetch (ES6 module, no Node.js dependencies).
 */

const BASE_PATH = '/api/v1/manager/tables';

function _csrfHeaders() {
    const token  = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    return (token && header) ? { [header]: token } : {};
}

function _headers() {
    return {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
        ..._csrfHeaders(),
    };
}

async function request(method, path, body, varName) {
    const opts = { method, headers: _headers() };
    if (body !== undefined) opts.body = JSON.stringify(body);
    const res = await fetch(BASE_PATH + path, opts);
    if (!res.ok) {
        const text = await res.text().catch(() => res.statusText);
        throw new Error(`[${varName}] ${method} ${path} → ${res.status}: ${text}`);
    }
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json') && res.status !== 204) return res.json();
    return null;
}

const tablesApi = {
    /** POST / */
    createTable(body) {
        return request('POST', `/`, body, 'tablesApi');
    },
    /** DELETE /{tableId} */
    deleteTable(tableId) {
        return request('DELETE', `/${tableId}`, 'tablesApi');
    },
    /** POST /{tableId}/open */
    openTable(tableId) {
        return request('POST', `/${tableId}/open`, 'tablesApi');
    },
    /** POST /orders/{orderId}/close */
    closeOrder(orderId) {
        return request('POST', `/orders/${orderId}/close`, 'tablesApi');
    },
    /** GET /{tableId}/history */
    getTableHistory(tableId, status, start, end) {
        const _p = new URLSearchParams();
        if (status !== undefined && status !== null) _p.append('status', status);
        if (start !== undefined && start !== null) _p.append('start', start);
        if (end !== undefined && end !== null) _p.append('end', end);
        const _qs = _p.toString() ? '?' + _p.toString() : '';
        return request('GET', `/${tableId}/history` + _qs, 'tablesApi');
    },
    /** POST /{tableId}/payment */
    processPayment(tableId, body) {
        return request('POST', `/${tableId}/payment`, body, 'tablesApi');
    },
    /** DELETE /{tableId}/payment/{paymentId} */
    deletePayment(tableId, paymentId) {
        return request('DELETE', `/${tableId}/payment/${paymentId}`, 'tablesApi');
    },
};

export { tablesApi };
