#!/bin/bash

# Simple validation script for Google Codelabs format
# This script checks if the codelab markdown follows proper format

CODELAB_FILE="codelabs/chapter1-current-state-analysis.md"

echo "🔍 Validating Google Codelabs format for: $CODELAB_FILE"
echo

if [ ! -f "$CODELAB_FILE" ]; then
    echo "❌ File not found: $CODELAB_FILE"
    exit 1
fi

echo "✅ File exists: $CODELAB_FILE"

# Check required metadata fields
echo
echo "📋 Checking metadata headers..."

REQUIRED_FIELDS=("author:" "summary:" "id:" "categories:" "environments:" "status:")
for field in "${REQUIRED_FIELDS[@]}"; do
    if head -10 "$CODELAB_FILE" | grep -q "^$field"; then
        echo "✅ Found: $field"
    else
        echo "❌ Missing: $field"
    fi
done

# Check for proper title format
echo
echo "📝 Checking title format..."
if head -15 "$CODELAB_FILE" | grep -q "^# "; then
    echo "✅ Found main title"
else
    echo "❌ Missing main title (# format)"
fi

# Check Duration settings
echo
echo "⏱️  Checking Duration settings..."
DURATION_COUNT=$(grep -c "^Duration:" "$CODELAB_FILE")
echo "✅ Found $DURATION_COUNT Duration settings"

# Calculate total duration
TOTAL_DURATION=0
while read -r duration; do
    TOTAL_DURATION=$((TOTAL_DURATION + duration))
done < <(grep "^Duration:" "$CODELAB_FILE" | cut -d' ' -f2)
echo "✅ Total duration: $TOTAL_DURATION minutes ($(($TOTAL_DURATION / 60))h $(($TOTAL_DURATION % 60))m)"

# Check for code blocks
echo
echo "💻 Checking code blocks..."
CODE_BLOCK_COUNT=$(grep -c "^\`\`\`" "$CODELAB_FILE")
echo "✅ Found $CODE_BLOCK_COUNT code block markers"

# Check for checkpoints
echo
echo "🎯 Checking validation checkpoints..."
CHECKPOINT_COUNT=$(grep -c "✅.*チェックポイント\|✅.*CheckPoint\|✅.*確認" "$CODELAB_FILE")
echo "✅ Found $CHECKPOINT_COUNT validation checkpoints"

# File size check
echo
echo "📊 File statistics..."
LINES=$(wc -l < "$CODELAB_FILE")
CHARS=$(wc -c < "$CODELAB_FILE")
WORDS=$(wc -w < "$CODELAB_FILE")
echo "✅ Lines: $LINES, Characters: $CHARS, Words: $WORDS"

echo
echo "🎉 Validation complete!"

# Summary
if [ $TOTAL_DURATION -ge 240 ] && [ $TOTAL_DURATION -le 360 ]; then
    echo "✅ Duration target met: $TOTAL_DURATION minutes (4-6 hour range)"
else
    echo "⚠️  Duration outside target: $TOTAL_DURATION minutes (target: 240-360 minutes)"
fi

if [ $DURATION_COUNT -ge 6 ]; then
    echo "✅ Sufficient section structure: $DURATION_COUNT sections"
else
    echo "⚠️  May need more sections: $DURATION_COUNT sections"
fi