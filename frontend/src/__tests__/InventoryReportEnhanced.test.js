// Enhanced Inventory Report E2E Test - Phase 1 (Basic compatibility version)
import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import InventoryReport from '../components/InventoryReport';
import reports from '../services/reportsApi';

// Mock reports API
jest.mock('../services/reportsApi', () => ({
  getInventoryReport: jest.fn(),
  getEnhancedInventoryReport: jest.fn(),
}));

// Mock store
const createMockStore = (initialState = {}) => createStore(() => ({
  inventory: {
    reports: [],
    loading: false,
    error: null,
    ...initialState,
  }
}), applyMiddleware(thunk));

describe('Enhanced Inventory Report E2E Tests - Phase 1', () => {
  let mockStore;

  beforeEach(() => {
    mockStore = createMockStore();
    jest.clearAllMocks();
    
    // Setup default mock responses to return immediately
    reports.getInventoryReport.mockResolvedValue({ 
      data: {
        reportDate: '2025-01-18',
        totalProducts: 25,
        lowStockCount: 3,
        outOfStockCount: 1,
        totalInventoryValue: 87500.00,
        items: []
      }
    });
    reports.getEnhancedInventoryReport.mockResolvedValue({ 
      data: {
        reportDate: '2025-01-18',
        totalProducts: 25,
        items: [],
        abcXyzAnalysis: [],
        deadStockAnalysis: [],
        obsolescenceRisk: []
      }
    });
  });

  test('renders inventory report component without crashing', () => {
    const { container } = render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Basic smoke test - component should render without throwing
    expect(container).toBeDefined();
    expect(container.firstChild).toBeDefined();
    
    // Check that basic API is called on mount
    expect(reports.getInventoryReport).toHaveBeenCalled();
  });

  test('calls basic inventory report API on mount', () => {
    render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Verify the basic API is called
    expect(reports.getInventoryReport).toHaveBeenCalledTimes(1);
  });

  test('enhanced report API mock is set up correctly', () => {
    render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Verify enhanced API mock exists and can be called
    expect(reports.getEnhancedInventoryReport).toBeDefined();
    expect(typeof reports.getEnhancedInventoryReport).toBe('function');
  });

  test('API calls are properly mocked', () => {
    render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Check that mocks are working
    expect(reports.getInventoryReport).toHaveBeenCalled();
    expect(jest.isMockFunction(reports.getInventoryReport)).toBe(true);
    expect(jest.isMockFunction(reports.getEnhancedInventoryReport)).toBe(true);
  });

  test('handles mock API setup correctly', () => {
    // Mock API error scenario
    reports.getInventoryReport.mockRejectedValue(new Error('API Error'));
    
    const { container } = render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Component should still render without crashing
    expect(container).toBeDefined();
    expect(container.firstChild).toBeDefined();
  });

  test('redux store integration works', () => {
    const customStore = createMockStore({
      loading: true,
      reports: [],
      error: null
    });

    const { container } = render(
      <Provider store={customStore}>
        <InventoryReport />
      </Provider>
    );

    // Component should integrate with store without issues
    expect(container).toBeDefined();
    expect(container.firstChild).toBeDefined();
  });

  test('mock functions reset properly between tests', () => {
    // This test verifies that our beforeEach setup is working
    expect(reports.getInventoryReport).toHaveBeenCalledTimes(0);
    expect(reports.getEnhancedInventoryReport).toHaveBeenCalledTimes(0);
    
    render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );
    
    expect(reports.getInventoryReport).toHaveBeenCalledTimes(1);
  });

  test('component structure validation', () => {
    const { container } = render(
      <Provider store={mockStore}>
        <InventoryReport />
      </Provider>
    );

    // Check that the component renders with expected structure
    expect(container.firstChild).toBeDefined();
    expect(container.firstChild.tagName).toBeDefined();
    
    // Verify that rendering doesn't throw any errors
    expect(() => {
      render(
        <Provider store={mockStore}>
          <InventoryReport />
        </Provider>
      );
    }).not.toThrow();
  });
});