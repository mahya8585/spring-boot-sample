import fs from 'fs';
import path from 'path';

describe('I18n Implementation Validation', () => {
  describe('Component Syntax Validation', () => {
    test('Dashboard component can be imported without syntax errors', () => {
      expect(() => {
        const Dashboard = require('../components/Dashboard');
        expect(Dashboard).toBeDefined();
      }).not.toThrow();
    });

    test('OrderList component can be imported without syntax errors', () => {
      expect(() => {
        const OrderList = require('../components/OrderList');
        expect(OrderList).toBeDefined();
      }).not.toThrow();
    });

    test('ReportsPage component can be imported without syntax errors', () => {
      expect(() => {
        const ReportsPage = require('../components/ReportsPage');
        expect(ReportsPage).toBeDefined();
      }).not.toThrow();
    });

    test('InventoryRotationMatrix component can be imported without syntax errors', () => {
      expect(() => {
        const InventoryRotationMatrix = require('../components/InventoryRotationMatrix');
        expect(InventoryRotationMatrix).toBeDefined();
      }).not.toThrow();
    });
  });

  describe('useI18n Hook Import Validation', () => {
    test('Dashboard imports useI18n correctly', () => {
      const dashboardSource = fs.readFileSync(
        path.join(__dirname, '../components/Dashboard.js'), 
        'utf8'
      );
      expect(dashboardSource).toContain("import { useI18n } from '../contexts/I18nContext'");
      expect(dashboardSource).toContain('const { t');
    });

    test('OrderList imports useI18n correctly', () => {
      const orderListSource = fs.readFileSync(
        path.join(__dirname, '../components/OrderList.js'), 
        'utf8'
      );
      expect(orderListSource).toContain("import { useI18n } from '../contexts/I18nContext'");
      expect(orderListSource).toContain('const { t');
    });

    test('ReportsPage imports useI18n correctly', () => {
      const reportsPageSource = fs.readFileSync(
        path.join(__dirname, '../components/ReportsPage.js'), 
        'utf8'
      );
      expect(reportsPageSource).toContain("import { useI18n } from '../contexts/I18nContext'");
      expect(reportsPageSource).toContain('const { t }');
    });

    test('InventoryRotationMatrix imports useI18n correctly', () => {
      const inventoryMatrixSource = fs.readFileSync(
        path.join(__dirname, '../components/InventoryRotationMatrix.js'), 
        'utf8'
      );
      expect(inventoryMatrixSource).toContain("import { useI18n } from '../contexts/I18nContext'");
      expect(inventoryMatrixSource).toContain('const { t }');
    });
  });

  describe('Translation Function Usage Validation', () => {
    test('Components use t() function instead of hardcoded Japanese strings', () => {
      const dashboardSource = fs.readFileSync(
        path.join(__dirname, '../components/Dashboard.js'), 
        'utf8'
      );
      
      // Should use t() for error messages
      expect(dashboardSource).toContain("t('dashboard.error.occurred'");
      
      // Should use t() for level labels
      expect(dashboardSource).toContain("t('book.level.beginner'");
      expect(dashboardSource).toContain("t('book.level.intermediate'");
      expect(dashboardSource).toContain("t('book.level.advanced'");
    });

    test('OrderList uses translation for detail button', () => {
      const orderListSource = fs.readFileSync(
        path.join(__dirname, '../components/OrderList.js'), 
        'utf8'
      );
      
      // Should use t() for detail button instead of hardcoded "詳細"
      expect(orderListSource).toContain("t('ui.detail'");
      expect(orderListSource).not.toMatch(/(?<!t\(.*)'詳細'/); // No standalone '詳細'
    });

    test('InventoryRotationMatrix uses translations', () => {
      const inventoryMatrixSource = fs.readFileSync(
        path.join(__dirname, '../components/InventoryRotationMatrix.js'), 
        'utf8'
      );
      
      // Should use t() for no data message
      expect(inventoryMatrixSource).toContain("t('ui.no.data'");
      
      // Should use t() for quadrant titles
      expect(inventoryMatrixSource).toContain("t('inventory.rotation.quadrant.star'");
      expect(inventoryMatrixSource).toContain("t('inventory.rotation.quadrant.question'");
      expect(inventoryMatrixSource).toContain("t('inventory.rotation.quadrant.cash.cow'");
      expect(inventoryMatrixSource).toContain("t('inventory.rotation.quadrant.dog'");
    });
  });

  describe('Message Key File Validation', () => {
    test('English message file contains required keys', () => {
      const englishMessages = fs.readFileSync(
        path.join(__dirname, '../../../backend/src/main/resources/messages_en.properties'), 
        'utf8'
      );
      
      const requiredKeys = [
        'dashboard.title=',
        'dashboard.total.books=',
        'order.management=',
        'ui.detail=',
        'book.level.beginner=',
        'inventory.rotation.matrix=',
        'inventory.rotation.quadrant.star=',
        'inventory.rotation.tooltip.turnover='
      ];
      
      requiredKeys.forEach(key => {
        expect(englishMessages).toContain(key);
      });
    });

    test('Japanese message file contains required keys', () => {
      const japaneseMessages = fs.readFileSync(
        path.join(__dirname, '../../../backend/src/main/resources/messages_ja.properties'), 
        'utf8'
      );
      
      const requiredKeys = [
        'dashboard.title=',
        'dashboard.total.books=',
        'order.management=',
        'ui.detail=',
        'book.level.beginner=',
        'inventory.rotation.matrix=',
        'inventory.rotation.quadrant.star=',
        'inventory.rotation.tooltip.turnover='
      ];
      
      requiredKeys.forEach(key => {
        expect(japaneseMessages).toContain(key);
      });
    });

    test('Both message files have same number of lines', () => {
      const englishMessages = fs.readFileSync(
        path.join(__dirname, '../../../backend/src/main/resources/messages_en.properties'), 
        'utf8'
      );
      const japaneseMessages = fs.readFileSync(
        path.join(__dirname, '../../../backend/src/main/resources/messages_ja.properties'), 
        'utf8'
      );
      
      const englishLines = englishMessages.split('\n').filter(line => line.trim() && !line.startsWith('#')).length;
      const japaneseLines = japaneseMessages.split('\n').filter(line => line.trim() && !line.startsWith('#')).length;
      
      expect(englishLines).toBe(japaneseLines);
    });
  });

  describe('I18nController Validation', () => {
    test('I18nController exposes new message keys', () => {
      const controllerSource = fs.readFileSync(
        path.join(__dirname, '../../../backend/src/main/java/com/techbookstore/app/controller/I18nController.java'), 
        'utf8'
      );
      
      const expectedKeys = [
        'ui.detail',
        'ui.no.data',
        'book.level.beginner',
        'inventory.rotation.quadrant.star',
        'inventory.rotation.tooltip.turnover'
      ];
      
      expectedKeys.forEach(key => {
        expect(controllerSource).toContain(`"${key}"`);
      });
    });
  });
});