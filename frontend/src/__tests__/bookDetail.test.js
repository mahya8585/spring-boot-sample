// Book detail management specific tests
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import BookEditForm from '../components/BookEditForm';

// Mock store with book detail state
const createMockStore = (initialState = {}) => createStore(() => ({
  books: {
    books: [],
    selectedBook: null,
    bookDetail: null,
    loading: false,
    detailLoading: false,
    error: null,
    isEditMode: false,
    ...initialState,
  }
}), applyMiddleware(thunk));

// Mock services/api
jest.mock('../services/api');

describe('Book Detail Management', () => {
  test('BookEditForm validates required fields', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: '',
      publisherName: 'Test Publisher',
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    const submitButton = screen.getByText('保存');
    fireEvent.click(submitButton);
    
    // Check if validation error appears
    expect(screen.getByText('タイトルは必須入力です')).toBeDefined();
  });

  test('BookEditForm validates price fields', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: 'Test Title',
      publisherName: 'Test Publisher',
      listPrice: -100,
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    const submitButton = screen.getByText('保存');
    fireEvent.click(submitButton);
    
    // Check if validation error appears
    expect(screen.getByText('定価は正の数値で入力してください')).toBeDefined();
  });

  test('BookEditForm shows read-only fields', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: 'Test Title',
      publisherName: 'Test Publisher',
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    expect(screen.getByDisplayValue('9784123456789')).toBeDefined();
    expect(screen.getByDisplayValue('Test Publisher')).toBeDefined();
  });

  test('BookEditForm renders form elements properly', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: 'Test Title',
      titleEn: 'Test English Title',
      publisherName: 'Test Publisher',
      listPrice: 2980,
      sellingPrice: 2682,
      pages: 300,
      level: 'BEGINNER',
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    expect(screen.getByText('書籍情報編集')).toBeDefined();
    expect(screen.getByDisplayValue('Test Title')).toBeDefined();
    expect(screen.getByDisplayValue('Test English Title')).toBeDefined();
    expect(screen.getByDisplayValue('2980')).toBeDefined();
    expect(screen.getByDisplayValue('2682')).toBeDefined();
    expect(screen.getByDisplayValue('300')).toBeDefined();
  });

  test('BookEditForm shows correct validation messages', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: '',
      publisherName: 'Test Publisher',
      listPrice: -100,
      sellingPrice: -50,
      pages: -10,
      edition: -1,
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    const submitButton = screen.getByText('保存');
    fireEvent.click(submitButton);
    
    // Check multiple validation messages
    expect(screen.getByText('タイトルは必須入力です')).toBeDefined();
    expect(screen.getByText('定価は正の数値で入力してください')).toBeDefined();
    expect(screen.getByText('販売価格は正の数値で入力してください')).toBeDefined();
    expect(screen.getByText('ページ数は正の整数で入力してください')).toBeDefined();
    expect(screen.getByText('版次は正の整数で入力してください')).toBeDefined();
  });

  test('BookEditForm validates URL fields', () => {
    const mockStore = createMockStore({ isEditMode: true });
    const mockBook = {
      id: 1,
      isbn13: '9784123456789',
      title: 'Test Title',
      publisherName: 'Test Publisher',
      sampleCodeUrl: 'invalid-url',
    };
    
    render(
      <Provider store={mockStore}>
        <BookEditForm book={mockBook} onCancel={() => {}} />
      </Provider>
    );
    
    const submitButton = screen.getByText('保存');
    fireEvent.click(submitButton);
    
    expect(screen.getByText('有効なURLを入力してください')).toBeDefined();
  });
});