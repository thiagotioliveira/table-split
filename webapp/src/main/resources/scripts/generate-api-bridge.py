#!/usr/bin/env python3
"""
Generates static/js/api-bridge.js from an OpenAPI YAML spec.

The output is a native browser ES6 module using fetch — no Node.js dependencies.
It mirrors the OpenAPI contract exactly: one JS method per operation.

Usage:
  python3 generate-api-bridge.py <spec.yaml> <base-path> <output.js> <var-name> [tag-filter]

Example:
  python3 generate-api-bridge.py order-v1.yaml /api/v1/manager/orders api-bridge.js ordersApi Orders
"""

import sys
import re
import os


# ---------------------------------------------------------------------------
# PyYAML bootstrap
# ---------------------------------------------------------------------------

def _load_yaml_module():
    try:
        import yaml
        return yaml
    except ImportError:
        import subprocess
        print("[generate-api-bridge] Installing PyYAML…", file=sys.stderr)
        subprocess.check_call(
            [sys.executable, "-m", "pip", "install", "pyyaml", "--quiet"]
        )
        import yaml
        return yaml
    except Exception as e:
        print(f"[generate-api-bridge] Error loading/installing PyYAML: {e}", file=sys.stderr)
        sys.exit(1)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _path_to_template_literal(path: str) -> str:
    """Convert /tables/{tableId}/place  →  /tables/${tableId}/place"""
    return re.sub(r'\{(\w+)\}', lambda m: '${' + m.group(1) + '}', path)


def _render_method(operation_id: str, http_method: str, path: str,
                   path_params: list, query_params: list, has_body: bool, var_name: str) -> str:

    js_path = _path_to_template_literal(path)
    http_upper = http_method.upper()

    # Argument list: path params → body → query params
    args = list(path_params)
    if has_body:
        args.append("body")
    args.extend(query_params)

    # Method body
    body_val = "body" if has_body else "undefined"
    if query_params:
        qs_lines = ["const _p = new URLSearchParams();"]
        for qp in query_params:
            qs_lines.append(
                f"if ({qp} !== undefined && {qp} !== null) _p.append('{qp}', {qp});"
            )
        qs_lines.append("const _qs = _p.toString() ? '?' + _p.toString() : '';")
        qs_lines.append(f"return request('{http_upper}', `{js_path}` + _qs, {body_val}, '{var_name}');")
        indented = "\n        ".join(qs_lines)
        body_block = f"        {indented}"
    else:
        body_block = f"        return request('{http_upper}', `{js_path}`, {body_val}, '{var_name}');"

    return (
        f"\n    /** {http_upper} {path} */\n"
        f"    {operation_id}({', '.join(args)}) {{\n"
        f"{body_block}\n"
        f"    }},"
    )


# ---------------------------------------------------------------------------
# Generator
# ---------------------------------------------------------------------------

_PREAMBLE = '''\
/**
 * {var_name} Bridge
 *
 * AUTO-GENERATED — do NOT edit manually.
 * Source: {spec_path}
 * Regenerated on every Maven build.
 *
 * Uses native browser fetch (ES6 module, no Node.js dependencies).
 */

const BASE_PATH = '{base_path}';

function _csrfHeaders() {{
    const token  = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    return (token && header) ? {{ [header]: token }} : {{}};
}}

function _headers() {{
    return {{
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
        ..._csrfHeaders(),
    }};
}}

class ApiError extends Error {{
    constructor(message, status, text) {{
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.text = text;
    }}
}}

async function request(method, path, body, varName) {{
    const opts = {{ method, headers: _headers() }};
    if (body !== undefined) opts.body = JSON.stringify(body);
    const res = await fetch(BASE_PATH + path, opts);
    if (!res.ok) {{
        const text = await res.text().catch(() => res.statusText);
        throw new ApiError(`[${{varName}}] ${{method}} ${{path}} → ${{res.status}}: ${{text}}`, res.status, text);
    }}
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json') && res.status !== 204) return res.json();
    return null;
}}

const {var_name} = {{'''

_POSTAMBLE = '''
}};\n
export {{ {var_name} }};
'''

HTTP_VERBS = {"get", "post", "put", "patch", "delete", "head", "options"}


def generate(spec_path: str, base_path: str, output_path: str, var_name: str, tag_filter: str = None) -> None:
    yaml = _load_yaml_module()

    with open(spec_path, encoding="utf-8") as fh:
        spec = yaml.safe_load(fh)

    methods: list[str] = []

    for path, path_item in spec.get("paths", {}).items():
        if not isinstance(path_item, dict):
            continue
        for http_method, operation in path_item.items():
            if http_method not in HTTP_VERBS:
                continue
            if not isinstance(operation, dict):
                continue
            
            if tag_filter:
                tags = operation.get("tags", [])
                if tag_filter not in tags:
                    continue

            operation_id = operation.get("operationId", "").strip()
            if not operation_id:
                continue

            parameters = operation.get("parameters", []) or []
            path_params  = [p["name"] for p in parameters if p.get("in") == "path"]
            query_params = [p["name"] for p in parameters if p.get("in") == "query"]
            has_body = "requestBody" in operation

            methods.append(
                _render_method(
                    operation_id, http_method, path,
                    path_params, query_params, has_body, var_name
                )
            )

    preamble = _PREAMBLE.format(
        spec_path=os.path.basename(spec_path),
        base_path=base_path,
        var_name=var_name
    )

    content = preamble + "".join(methods) + _POSTAMBLE.format(var_name=var_name)

    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as fh:
        fh.write(content)

    suffix = f" (filtered by tag: {tag_filter})" if tag_filter else ""
    print(f"[generate-api-bridge] Generated {output_path} ({len(methods)} operations){suffix}")


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    if len(sys.argv) < 5:
        print(f"Usage: {sys.argv[0]} <spec.yaml> <base-path> <output.js> <var-name> [tag-filter]",
              file=sys.stderr)
        sys.exit(1)
    
    tag = sys.argv[5] if len(sys.argv) > 5 else None
    generate(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], tag)
