#!/bin/sh
# Adds .js extensions to relative imports in generated OpenAPI JS client files.
# Required for browser compatibility with native ES6 modules.
#
# Handles:
#   import X from '../path/to/File'   -> import X from '../path/to/File.js'
#   import X from "./path/to/File"    -> import X from "./path/to/File.js"
#
# Skips files that already have .js extension in their imports.

TARGET_DIR="$1"

if [ -z "$TARGET_DIR" ]; then
  echo "Usage: $0 <target-dir>"
  exit 1
fi

find "$TARGET_DIR" -name "*.js" | while read -r file; do
  # Use perl for consistent behavior across macOS (BSD sed) and Linux (GNU sed)
  perl -i -pe "s|from (['\"])(\\.{1,2}/[^'\"]*?)(\\.js)?\\1|from \$1\$2.js\$1|g" "$file"
done

echo "JS imports fixed in: $TARGET_DIR"
