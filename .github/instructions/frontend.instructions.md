# Frontend Development Instructions - React Modernization

## 🎯 Context
This file provides specific instructions for working with the **TechBookStore frontend**, a React 16.13.1 + Material-UI 4.x application that demonstrates the journey from legacy frontend patterns to modern React 18+ development.

## 🏗️ Current Architecture

### Technology Stack (Legacy - Intentional)
```
- React: 16.13.1 (with legacy patterns)
- Material-UI: 4.11.4 (deprecated, target MUI 5.x)
- Redux: 4.0.5 + React-Redux 7.2.0
- React Router: 5.2.0 (target v6+)
- Build Tool: Create React App 4.0.3
- Testing: Jest + React Testing Library
- HTTP Client: Axios 1.11.0
- Charts: Recharts 1.8.5
```

### Component Architecture
```
src/
├── App.js                          # Main application with routing
├── index.js                        # React application entry point
├── theme.js                        # Material-UI theme configuration
├── setupTests.js                   # Jest test configuration
├── components/                     # UI Components (25+ components)
│   ├── Dashboard.js               # Executive dashboard with metrics
│   ├── BookList.js                # Book inventory management
│   ├── BookDetail.js              # Individual book information
│   ├── BookEditForm.js            # Book editing interface
│   ├── InventoryList.js           # Stock management interface
│   ├── InventoryReport.js         # Inventory analytics
│   ├── CustomerList.js            # Customer management
│   ├── CustomerDetail.js          # Customer profile view
│   ├── CustomerForm.js            # Customer creation/editing
│   ├── OrderList.js               # Order management interface
│   ├── ReportsPage.js             # Analytics dashboard
│   ├── SalesReport.js             # Sales analytics
│   ├── ExecutiveDashboard.js      # High-level business metrics
│   ├── LanguageSelector.js        # I18n language switching
│   ├── ErrorBoundary.js           # Error handling component
│   ├── LoadingSpinner.js          # Loading state component
│   └── dashboard/                 # Dashboard-specific components
├── contexts/                       # React Context providers
│   └── I18nContext.js             # Internationalization context
├── hooks/                          # Custom React hooks
│   ├── useApi.js                  # API interaction hook
│   ├── useLocalStorage.js         # Local storage management
│   └── useDebounce.js             # Debouncing utility
├── services/                       # API and external services
│   ├── api.js                     # Axios configuration and endpoints
│   ├── bookService.js             # Book-related API calls
│   ├── inventoryService.js        # Inventory API integration
│   └── customerService.js         # Customer management APIs
└── store/                          # Redux state management
    ├── index.js                   # Store configuration
    ├── reducers/                  # Redux reducers
    └── actions/                   # Redux action creators
```

## 🎓 Educational Objectives

### Learning Goals for Frontend Modernization
1. **Legacy Pattern Recognition**: Identify outdated React 16 patterns
2. **Component Migration**: Convert class components to functional components with hooks
3. **Material-UI Evolution**: Migrate from Material-UI 4.x to MUI 5.x
4. **State Management**: Modernize Redux patterns or migrate to Context API
5. **Router Upgrade**: Upgrade React Router v5 to v6+
6. **Performance Optimization**: Implement modern React performance patterns

## 🔧 Development Guidelines

### Component Patterns

#### Current Pattern (Legacy React 16)
```javascript
// Class component with lifecycle methods
import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import { AppBar, Toolbar, Typography } from '@material-ui/core';

const styles = (theme) => ({
  root: {
    flexGrow: 1,
  },
  title: {
    flexGrow: 1,
  },
});

class Header extends Component {
  constructor(props) {
    super(props);
    this.state = {
      user: null,
      loading: true,
    };
  }

  componentDidMount() {
    this.fetchUserData();
  }

  fetchUserData = async () => {
    try {
      const response = await fetch('/api/user');
      const user = await response.json();
      this.setState({ user, loading: false });
    } catch (error) {
      this.setState({ loading: false });
    }
  };

  render() {
    const { classes } = this.props;
    const { user, loading } = this.state;

    return (
      <AppBar position="static" className={classes.root}>
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            TechBookStore
          </Typography>
          {!loading && user && (
            <Typography variant="body2">
              {user.name}
            </Typography>
          )}
        </Toolbar>
      </AppBar>
    );
  }
}

export default withStyles(styles)(Header);
```

#### Modern Pattern (Target React 18+)
```javascript
// Functional component with hooks
import React, { useState, useEffect } from 'react';
import { styled } from '@mui/material/styles';
import { AppBar, Toolbar, Typography, Skeleton } from '@mui/material';
import { useApi } from '../hooks/useApi';

const StyledAppBar = styled(AppBar)(({ theme }) => ({
  '& .MuiToolbar-root': {
    justifyContent: 'space-between',
  },
}));

const Header = () => {
  const { data: user, loading, error } = useApi('/api/user');

  return (
    <StyledAppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="h1">
          TechBookStore
        </Typography>
        {loading ? (
          <Skeleton variant="text" width={100} />
        ) : (
          user && (
            <Typography variant="body2">
              {user.name}
            </Typography>
          )
        )}
      </Toolbar>
    </StyledAppBar>
  );
};

export default Header;
```

### State Management Evolution

#### Current Redux Pattern (Legacy)
```javascript
// Action creators with thunk
export const fetchBooks = () => {
  return async (dispatch) => {
    dispatch({ type: 'FETCH_BOOKS_START' });
    try {
      const response = await api.get('/books');
      dispatch({ type: 'FETCH_BOOKS_SUCCESS', payload: response.data });
    } catch (error) {
      dispatch({ type: 'FETCH_BOOKS_ERROR', payload: error.message });
    }
  };
};

// Reducer
const bookReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'FETCH_BOOKS_START':
      return { ...state, loading: true };
    case 'FETCH_BOOKS_SUCCESS':
      return { ...state, loading: false, books: action.payload };
    case 'FETCH_BOOKS_ERROR':
      return { ...state, loading: false, error: action.payload };
    default:
      return state;
  }
};
```

#### Modern Context + Hooks Pattern (Target)
```javascript
// Context with useReducer
const BookContext = createContext();

const bookReducer = (state, action) => {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload };
    case 'SET_BOOKS':
      return { ...state, books: action.payload, loading: false };
    case 'SET_ERROR':
      return { ...state, error: action.payload, loading: false };
    default:
      return state;
  }
};

export const BookProvider = ({ children }) => {
  const [state, dispatch] = useReducer(bookReducer, {
    books: [],
    loading: false,
    error: null,
  });

  const fetchBooks = useCallback(async () => {
    dispatch({ type: 'SET_LOADING', payload: true });
    try {
      const response = await api.get('/books');
      dispatch({ type: 'SET_BOOKS', payload: response.data });
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: error.message });
    }
  }, []);

  return (
    <BookContext.Provider value={{ ...state, fetchBooks }}>
      {children}
    </BookContext.Provider>
  );
};

export const useBooks = () => {
  const context = useContext(BookContext);
  if (!context) {
    throw new Error('useBooks must be used within BookProvider');
  }
  return context;
};
```

### Custom Hooks Implementation

#### API Management Hook
```javascript
// hooks/useApi.js
import { useState, useEffect } from 'react';
import { api } from '../services/api';

export const useApi = (url, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await api.get(url, options);
        setData(response.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [url, JSON.stringify(options)]);

  return { data, loading, error };
};
```

#### Local Storage Hook
```javascript
// hooks/useLocalStorage.js
import { useState, useEffect } from 'react';

export const useLocalStorage = (key, initialValue) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  });

  const setValue = (value) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(`Error setting localStorage key "${key}":`, error);
    }
  };

  return [storedValue, setValue];
};
```

## 🌐 Internationalization (I18n)

### Current I18n Implementation
```javascript
// contexts/I18nContext.js
import React, { createContext, useContext, useState } from 'react';

const I18nContext = createContext();

const translations = {
  ja: {
    'app.title': 'TechBookStore - 技術専門書店在庫管理システム',
    'menu.dashboard': 'ダッシュボード',
    'menu.books': '書籍管理',
    'menu.inventory': '在庫管理',
    'menu.customers': '顧客管理',
    'menu.reports': 'レポート',
  },
  en: {
    'app.title': 'TechBookStore - Technical Book Inventory System',
    'menu.dashboard': 'Dashboard',
    'menu.books': 'Books',
    'menu.inventory': 'Inventory',
    'menu.customers': 'Customers',
    'menu.reports': 'Reports',
  },
};

export const I18nProvider = ({ children }) => {
  const [language, setLanguage] = useState('ja');

  const t = (key, fallback = key) => {
    return translations[language]?.[key] || fallback;
  };

  return (
    <I18nContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </I18nContext.Provider>
  );
};

export const useI18n = () => {
  const context = useContext(I18nContext);
  if (!context) {
    throw new Error('useI18n must be used within I18nProvider');
  }
  return context;
};
```

### Usage in Components
```javascript
import { useI18n } from '../contexts/I18nContext';

const BookList = () => {
  const { t } = useI18n();

  return (
    <div>
      <Typography variant="h4">
        {t('menu.books', '書籍管理')}
      </Typography>
      {/* Component content */}
    </div>
  );
};
```

## 🎨 Styling and Theming

### Current Material-UI 4.x Theme
```javascript
// theme.js
import { createMuiTheme } from '@material-ui/core/styles';

const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
  },
  overrides: {
    MuiButton: {
      root: {
        textTransform: 'none',
      },
    },
  },
});

export default theme;
```

### Target MUI 5.x Theme
```javascript
// theme.js (modernized)
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
  },
});

export default theme;
```

## 🧪 Testing Patterns

### Component Testing
```javascript
// __tests__/BookList.test.js
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import BookList from '../BookList';
import { I18nProvider } from '../contexts/I18nContext';

const server = setupServer(
  rest.get('/api/books', (req, res, ctx) => {
    return res(
      ctx.json([
        { id: 1, title: 'Spring Boot in Action', category: 'Backend' },
        { id: 2, title: 'React Design Patterns', category: 'Frontend' },
      ])
    );
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

const renderWithProviders = (component) => {
  return render(
    <I18nProvider>
      {component}
    </I18nProvider>
  );
};

test('renders book list with correct data', async () => {
  renderWithProviders(<BookList />);

  expect(screen.getByText('Loading...')).toBeInTheDocument();

  await waitFor(() => {
    expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
    expect(screen.getByText('React Design Patterns')).toBeInTheDocument();
  });
});

test('handles search functionality', async () => {
  const user = userEvent.setup();
  renderWithProviders(<BookList />);

  await waitFor(() => {
    expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
  });

  const searchInput = screen.getByRole('textbox', { name: /search/i });
  await user.type(searchInput, 'Spring');

  expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
  expect(screen.queryByText('React Design Patterns')).not.toBeInTheDocument();
});
```

### Hook Testing
```javascript
// __tests__/useApi.test.js
import { renderHook, waitFor } from '@testing-library/react';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import { useApi } from '../hooks/useApi';

const server = setupServer(
  rest.get('/api/test', (req, res, ctx) => {
    return res(ctx.json({ message: 'success' }));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('fetches data successfully', async () => {
  const { result } = renderHook(() => useApi('/api/test'));

  expect(result.current.loading).toBe(true);
  expect(result.current.data).toBe(null);

  await waitFor(() => {
    expect(result.current.loading).toBe(false);
    expect(result.current.data).toEqual({ message: 'success' });
  });
});
```

## 📊 Performance Optimization

### Code Splitting
```javascript
// App.js with lazy loading
import React, { Suspense } from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LoadingSpinner from './components/LoadingSpinner';

// Lazy load components
const Dashboard = React.lazy(() => import('./components/Dashboard'));
const BookList = React.lazy(() => import('./components/BookList'));
const CustomerList = React.lazy(() => import('./components/CustomerList'));

const App = () => {
  return (
    <Router>
      <Suspense fallback={<LoadingSpinner />}>
        <Switch>
          <Route exact path="/" component={Dashboard} />
          <Route path="/books" component={BookList} />
          <Route path="/customers" component={CustomerList} />
        </Switch>
      </Suspense>
    </Router>
  );
};
```

### Memoization Patterns
```javascript
// Optimized component with React.memo and useMemo
import React, { memo, useMemo } from 'react';

const BookCard = memo(({ book, onEdit, onDelete }) => {
  const formattedPrice = useMemo(() => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
    }).format(book.price);
  }, [book.price]);

  return (
    <Card>
      <CardContent>
        <Typography variant="h6">{book.title}</Typography>
        <Typography variant="body2">{formattedPrice}</Typography>
      </CardContent>
      <CardActions>
        <Button onClick={() => onEdit(book.id)}>Edit</Button>
        <Button onClick={() => onDelete(book.id)}>Delete</Button>
      </CardActions>
    </Card>
  );
});

BookCard.displayName = 'BookCard';
```

## 🛠️ Build and Development

### Development Commands
```bash
# Start development server
npm start

# Run tests
npm test

# Run tests with coverage
npm test -- --coverage

# Build for production
npm run build

# Lint code
npm run lint

# Build with legacy OpenSSL (Node 17+ compatibility)
NODE_OPTIONS='--openssl-legacy-provider' npm run build
```

### Package.json Configuration
```json
{
  "scripts": {
    "start": "react-scripts start",
    "build": "NODE_OPTIONS='--openssl-legacy-provider' react-scripts build",
    "test": "react-scripts test",
    "test:ci": "react-scripts test --coverage --silent --watchAll=false",
    "lint": "eslint src --ext .js,.jsx"
  },
  "proxy": "http://localhost:8080",
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}
```

## 🎯 Workshop Integration

### GitHub Copilot Prompts for Frontend
```
"Convert this Material-UI 4.x component to MUI 5.x with modern styling patterns"

"Transform this class component to a functional component using modern React hooks"

"Create a custom hook for managing form state with validation"

"Optimize this component for performance using React.memo and useMemo"

"Add comprehensive unit tests for this React component using React Testing Library"

"Implement proper error boundaries and loading states for this component"
```

### Common Migration Patterns
1. **Class to Functional**: Convert `componentDidMount` → `useEffect`
2. **Material-UI 4 → MUI 5**: Update imports and styling APIs
3. **Redux → Context**: Simplify state management for smaller applications
4. **React Router v5 → v6**: Update routing patterns and navigation

## 🚨 Important Notes

### Legacy Considerations
- **React 16.13.1**: Missing concurrent features, automatic batching
- **Material-UI 4.x**: Deprecated styling system, different component APIs
- **Create React App 4**: Older webpack configuration, Node.js compatibility issues

### Migration Challenges
- **Breaking Changes**: Material-UI 4 → MUI 5 has significant API changes
- **Bundle Size**: Modern builds may be larger due to polyfills
- **Browser Support**: Need to maintain compatibility with older browsers

### Development Environment
- **Node.js**: Requires legacy OpenSSL provider for React Scripts
- **Hot Reload**: May be slower with legacy CRA version
- **ESLint**: Custom configuration for React hooks and modern patterns

This frontend serves as an excellent foundation for learning modern React development while understanding the evolution from legacy patterns to contemporary best practices.
