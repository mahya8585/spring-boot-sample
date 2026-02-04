import React, { useEffect, useState } from 'react';
import {
  Grid,
  Typography,
  Box,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Chip,
  CircularProgress,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { Warning, Storage, ShoppingCart } from '@material-ui/icons';
import { inventoryApi, booksApi } from '../services/api';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
  card: {
    height: '100%',
  },
  statCard: {
    padding: theme.spacing(2),
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(2),
  },
  statIcon: {
    fontSize: 40,
    color: theme.palette.primary.main,
  },
  statNumber: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.text.primary,
  },
  alertCard: {
    padding: theme.spacing(2),
  },
  alertIcon: {
    color: theme.palette.warning.main,
    marginRight: theme.spacing(1),
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
}));

function Dashboard() {
  const classes = useStyles();
  const { t } = useI18n();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    totalBooks: 0,
    lowStockItems: 0,
    outOfStock: 0,
    pendingOrders: 0,
  });
  const [recentBooks, setRecentBooks] = useState([]);
  const [lowStockAlerts, setLowStockAlerts] = useState([]);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch all data in parallel
        const [booksResponse, inventoryResponse, alertsResponse, outOfStockResponse] = await Promise.all([
          booksApi.getBooks({ page: 0, size: 5, sortBy: 'id', sortDir: 'desc' }),
          inventoryApi.getInventory(),
          inventoryApi.getInventoryAlerts(),
          inventoryApi.getOutOfStockItems(),
        ]);

        // Calculate statistics
        const inventory = inventoryResponse.data;
        const totalBooks = inventory.length;
        const lowStockItems = alertsResponse.data.length;
        const outOfStock = outOfStockResponse.data.length;
        const pendingOrders = inventory.reduce((sum, item) => sum + (item.reservedCount || 0), 0);

        setStats({
          totalBooks,
          lowStockItems,
          outOfStock,
          pendingOrders,
        });

        setRecentBooks(booksResponse.data.content || booksResponse.data || []);
        setLowStockAlerts(alertsResponse.data || []);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError(err.response?.data?.message || err.message || t('dashboard.error.occurred', 'エラーが発生しました'));
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const getLevelLabel = (level) => {
    switch (level) {
      case 'BEGINNER':
        return t('book.level.beginner', '初級');
      case 'INTERMEDIATE':
        return t('book.level.intermediate', '中級');
      case 'ADVANCED':
        return t('book.level.advanced', '上級');
      default:
        return '-';
    }
  };

  const getLevelColor = (level) => {
    switch (level) {
      case 'BEGINNER':
        return 'primary';
      case 'INTERMEDIATE':
        return 'secondary';
      case 'ADVANCED':
        return 'default';
      default:
        return 'default';
    }
  };

  if (loading) {
    return (
      <div className={classes.loading}>
        <CircularProgress />
      </div>
    );
  }

  if (error) {
    return (
      <Box p={2}>
        <Typography color="error">
          {error}
        </Typography>
      </Box>
    );
  }

  return (
    <div className={classes.root}>
      <Typography variant="h4" gutterBottom>
        {t('dashboard.title', 'ダッシュボード')}
      </Typography>

      <Grid container spacing={3}>
        {/* Statistics Cards */}
        <Grid item xs={12} sm={6} md={3}>
          <Card className={classes.card}>
            <CardContent className={classes.statCard}>
              <Storage className={classes.statIcon} />
              <Box>
                <Typography className={classes.statNumber}>
                  {stats?.totalBooks || 0}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {t('dashboard.total.books', '総書籍数')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className={classes.card}>
            <CardContent className={classes.statCard}>
              <Warning className={classes.statIcon} style={{ color: '#ff9800' }} />
              <Box>
                <Typography className={classes.statNumber}>
                  {stats?.lowStockItems || 0}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {t('dashboard.low.stock', '在庫不足')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className={classes.card}>
            <CardContent className={classes.statCard}>
              <Warning className={classes.statIcon} style={{ color: '#f44336' }} />
              <Box>
                <Typography className={classes.statNumber}>
                  {stats?.outOfStock || 0}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {t('dashboard.out.of.stock', '在庫切れ')}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className={classes.card}>
            <CardContent className={classes.statCard}>
              <ShoppingCart className={classes.statIcon} />
              <Box>
                <Typography className={classes.statNumber}>
                  {stats?.pendingOrders || 0}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  予約待ち
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Books */}
        <Grid item xs={12} md={6}>
          <Card className={classes.card}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('dashboard.recent.books', '最近追加された書籍')}
              </Typography>
              <List>
                {recentBooks.map((book) => (
                  <ListItem key={book.id}>
                    <ListItemText
                      primary={book.title}
                      secondary={`ISBN: ${book.isbn13}`}
                    />
                    <Chip
                      label={getLevelLabel(book.level)}
                      color={getLevelColor(book.level)}
                      size="small"
                    />
                  </ListItem>
                ))}
                {recentBooks.length === 0 && (
                  <ListItem>
                    <ListItemText 
                      primary={t('ui.no.data', 'データがありません')}
                      secondary={t('dashboard.no.recent.books', '最近追加された書籍はありません')}
                    />
                  </ListItem>
                )}
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Inventory Alerts */}
        <Grid item xs={12} md={6}>
          <Card className={classes.card}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {t('dashboard.inventory.alerts', '在庫アラート')}
              </Typography>
              <List>
                {lowStockAlerts.map((alert) => (
                  <ListItem key={alert.id}>
                    <Warning className={classes.alertIcon} />
                    <ListItemText
                      primary={alert.bookTitle}
                      secondary={`在庫数: ${alert.availableStock} / 発注点: ${alert.reorderPoint || '-'}`}
                    />
                  </ListItem>
                ))}
                {lowStockAlerts.length === 0 && (
                  <ListItem>
                    <ListItemText 
                      primary={t('dashboard.no.alerts', 'アラートなし')}
                      secondary={t('dashboard.no.alerts.description', '現在、在庫アラートはありません')}
                    />
                  </ListItem>
                )}
              </List>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </div>
  );
}

export default Dashboard;