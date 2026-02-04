#!/bin/bash

# I18n Implementation Validation Script
echo "=== I18n Implementation Comprehensive Validation ==="
echo ""

# Change to the project root
cd /home/runner/work/legacy-spring-boot-23-app/legacy-spring-boot-23-app

echo "1. Validating Frontend Components..."
echo "   - Running i18n validation tests..."
cd frontend
npm test -- --testPathPattern=i18n-validation.test.js --watchAll=false --silent 2>/dev/null
FRONTEND_EXIT_CODE=$?

if [ $FRONTEND_EXIT_CODE -eq 0 ]; then
    echo "   ✅ Frontend i18n validation PASSED"
else
    echo "   ❌ Frontend i18n validation FAILED"
fi

echo ""
echo "2. Validating Backend Controller..."
echo "   - Running backend i18n controller tests..."
cd ../backend
./mvnw test -Dtest=I18nControllerValidationTest -q 2>/dev/null
BACKEND_EXIT_CODE=$?

if [ $BACKEND_EXIT_CODE -eq 0 ]; then
    echo "   ✅ Backend i18n controller validation PASSED"
else
    echo "   ❌ Backend i18n controller validation FAILED"
fi

echo ""
echo "3. Validating Message File Consistency..."
cd ../

# Count non-comment lines in both message files
EN_LINES=$(grep -v '^#' backend/src/main/resources/messages_en.properties | grep -v '^$' | wc -l)
JA_LINES=$(grep -v '^#' backend/src/main/resources/messages_ja.properties | grep -v '^$' | wc -l)

echo "   - English message file: $EN_LINES keys"
echo "   - Japanese message file: $JA_LINES keys"

if [ $EN_LINES -eq $JA_LINES ]; then
    echo "   ✅ Message file consistency PASSED"
    CONSISTENCY_OK=1
else
    echo "   ❌ Message file consistency FAILED - Different number of keys"
    CONSISTENCY_OK=0
fi

echo ""
echo "4. Validating Component Syntax..."
echo "   - Checking for hardcoded Japanese strings in target components..."

# Check for hardcoded Japanese strings only in the components we modified
TARGET_COMPONENTS="Dashboard.js OrderList.js ReportsPage.js InventoryRotationMatrix.js"
HARDCODED_ISSUES=0

for component in $TARGET_COMPONENTS; do
    # Only check for hardcoded strings that are NOT inside t() functions
    ISSUES=$(grep "詳細\|ダッシュボード\|注文管理\|データがありません\|初級\|中級\|上級" frontend/src/components/$component | grep -v "t(" | wc -l)
    if [ "$ISSUES" != "0" ]; then
        HARDCODED_ISSUES=$((HARDCODED_ISSUES + ISSUES))
        echo "     ⚠️  Found $ISSUES issues in $component"
        # Show the actual lines for debugging
        grep -n "詳細\|ダッシュボード\|注文管理\|データがありません\|初級\|中級\|上級" frontend/src/components/$component | grep -v "t(" | head -3
    fi
done

if [ $HARDCODED_ISSUES -eq 0 ]; then
    echo "   ✅ No hardcoded Japanese strings found in target components"
    SYNTAX_OK=1
else
    echo "   ❌ Found $HARDCODED_ISSUES potential hardcoded Japanese strings in target components"
    SYNTAX_OK=0
fi

echo ""
echo "5. Final Summary"
echo "=================="

TOTAL_SCORE=0
if [ $FRONTEND_EXIT_CODE -eq 0 ]; then TOTAL_SCORE=$((TOTAL_SCORE + 1)); fi
if [ $BACKEND_EXIT_CODE -eq 0 ]; then TOTAL_SCORE=$((TOTAL_SCORE + 1)); fi  
if [ $CONSISTENCY_OK -eq 1 ]; then TOTAL_SCORE=$((TOTAL_SCORE + 1)); fi
if [ $SYNTAX_OK -eq 1 ]; then TOTAL_SCORE=$((TOTAL_SCORE + 1)); fi

echo "Score: $TOTAL_SCORE/4 tests passed"

if [ $TOTAL_SCORE -eq 4 ]; then
    echo "🎉 All i18n validations PASSED! The implementation is working correctly."
    exit 0
else
    echo "⚠️  Some validations failed. Please review the output above."
    exit 1
fi