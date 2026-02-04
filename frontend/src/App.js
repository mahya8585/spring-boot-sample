import React, { useState } from 'react';
import { Switch, Route, useHistory } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Drawer, List, ListItem, Box, CssBaseline, Button } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { Dashboard, Book, Storage, ShoppingCart, People, Assessment } from '@material-ui/icons';
import { I18nProvider, useI18n } from './contexts/I18nContext';
import LanguageSelector from './components/LanguageSelector';
import ErrorBoundary from './components/ErrorBoundary';
import BookList from './components/BookList';
import InventoryList from './components/InventoryList';
import OrderList from './components/OrderList';
import CustomerList from './components/CustomerList';
import CustomerDetail from './components/CustomerDetail';
import CustomerForm from './components/CustomerForm';
import DashboardPage from './components/Dashboard';
import ReportsPage from './components/ReportsPage';
import SalesReport from './components/SalesReport';
import ExecutiveDashboard from './components/ExecutiveDashboard';
import InventoryReport from './components/InventoryReport';
import SalesAnalysisPage from './components/SalesAnalysisPage';
import InventoryAnalysisPage from './components/InventoryAnalysisPage';
import TechTrendReportPage from './components/TechTrendReportPage';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  appBar: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: drawerWidth,
  },
  appBarTitle: {
    flexGrow: 1,
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  toolbar: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    backgroundColor: theme.palette.background.default,
    padding: theme.spacing(3),
  },
}));

function AppContent() {
  const classes = useStyles();
  const history = useHistory();
  const [selectedMenu, setSelectedMenu] = useState('dashboard');
  const { t } = useI18n();

  const menuItems = [
    { id: 'dashboard', label: t('menu.dashboard', 'ダッシュボード'), icon: <Dashboard />, path: '/' },
    { id: 'books', label: t('menu.books', '書籍管理'), icon: <Book />, path: '/books' },
    { id: 'inventory', label: t('inventory.title', '在庫管理'), icon: <Storage />, path: '/inventory' },
    { id: 'orders', label: t('menu.orders', '注文管理'), icon: <ShoppingCart />, path: '/orders' },
    { id: 'customers', label: t('menu.customers', '顧客管理'), icon: <People />, path: '/customers' },
    { id: 'reports', label: t('menu.reports', 'レポート'), icon: <Assessment />, path: '/reports' },
  ];

  const handleMenuClick = (item) => {
    console.log('Menu clicked:', item);
    setSelectedMenu(item.id);
    history.push(item.path);
  };

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <Typography variant="h6" noWrap className={classes.appBarTitle}>
            {t('app.title', 'TechBookStore - 技術専門書店在庫管理システム')}
          </Typography>
          <LanguageSelector />
        </Toolbar>
      </AppBar>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{
          paper: classes.drawerPaper,
        }}
        anchor="left"
      >
        <div className={classes.toolbar} />
        <List>
          {menuItems.map((item) => (
            <ListItem key={item.id} style={{ padding: 0 }}>
              <Button
                fullWidth
                startIcon={item.icon}
                onClick={() => handleMenuClick(item)}
                style={{
                  justifyContent: 'flex-start',
                  padding: '8px 16px',
                  textTransform: 'none',
                  color: selectedMenu === item.id ? '#1976d2' : 'inherit',
                  backgroundColor: selectedMenu === item.id ? 'rgba(25, 118, 210, 0.08)' : 'transparent'
                }}
              >
                {item.label}
              </Button>
            </ListItem>
          ))}
        </List>
      </Drawer>
      <main className={classes.content}>
        <div className={classes.toolbar} />
        <Box>
          <ErrorBoundary>
            <Switch>
              <Route exact path="/" component={DashboardPage} />
              <Route path="/books" component={BookList} />
              <Route path="/inventory" component={InventoryList} />
              <Route path="/orders" component={OrderList} />
              <Route exact path="/customers" component={CustomerList} />
              <Route exact path="/customers/new" component={CustomerForm} />
              <Route exact path="/customers/:id" component={CustomerDetail} />
              <Route exact path="/customers/:id/edit" component={CustomerForm} />
              <Route exact path="/reports" component={ReportsPage} />
              <Route exact path="/reports/sales" component={SalesReport} />
              <Route exact path="/reports/dashboard" component={ExecutiveDashboard} />
              <Route exact path="/reports/inventory" component={InventoryReport} />
              <Route exact path="/reports/sales/analysis" component={SalesAnalysisPage} />
              <Route exact path="/reports/inventory/analysis" component={InventoryAnalysisPage} />
              <Route path="/reports/customers" render={() => <div>{t('report.customers.under.development', '顧客分析レポート（開発中）')}</div>} />
              <Route path="/reports/tech-trends" component={TechTrendReportPage} />
            </Switch>
          </ErrorBoundary>
        </Box>
      </main>
    </div>
  );
}

function App() {
  return (
    <I18nProvider>
      <AppContent />
    </I18nProvider>
  );
}

export default App;