import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Container,
  Breadcrumbs,
  Link,
  makeStyles,
  Paper,
  IconButton,
  Chip,
  CircularProgress,
  Button,
} from '@material-ui/core';
import { Alert, AlertTitle } from '@material-ui/lab';
import {
  Dashboard,
  TrendingUp,
  TrendingDown,
  TrendingFlat,
  Refresh,
  AttachMoney,
  ShoppingCart,
  People,
  Storage,
} from '@material-ui/icons';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import reports from '../services/reportsApi';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  title: {
    marginBottom: theme.spacing(3),
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  titleText: {
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(1),
  },
  kpiCard: {
    padding: theme.spacing(2),
    textAlign: 'center',
    height: '100%',
  },
  kpiIcon: {
    fontSize: 48,
    marginBottom: theme.spacing(1),
  },
  kpiValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: theme.spacing(0.5),
  },
  kpiLabel: {
    color: theme.palette.text.secondary,
    fontSize: '0.875rem',
    marginBottom: theme.spacing(1),
  },
  kpiTrend: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: theme.spacing(0.5),
  },
  chartCard: {
    padding: theme.spacing(2),
    height: '100%',
  },
  chartTitle: {
    marginBottom: theme.spacing(2),
    fontWeight: 600,
  },
  trendChip: {
    fontSize: '0.75rem',
  },
  loadingBox: {
    display: 'flex',
    justifyContent: 'center',
    padding: theme.spacing(4),
  },
  breadcrumbs: {
    marginBottom: theme.spacing(2),
  },
  lastUpdated: {
    color: theme.palette.text.secondary,
    fontSize: '0.75rem',
    textAlign: 'center',
    marginTop: theme.spacing(1),
  },
}));

const DashboardReport = () => {
  const classes = useStyles();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(new Date());

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await reports.getDashboardKpis();
      setDashboardData(data);
      setLastUpdated(new Date());
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setError(error.message || 'ダッシュボードデータの取得に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
    // Auto-refresh every 5 minutes
    const interval = setInterval(fetchDashboardData, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, []);

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(value);
  };

  const formatNumber = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(value);
  };

  const formatPercentage = (value) => {
    return `${value > 0 ? '+' : ''}${value.toFixed(1)}%`;
  };

  const getTrendIcon = (trend) => {
    switch (trend) {
      case 'UP':
        return <TrendingUp style={{ color: '#4caf50' }} />;
      case 'DOWN':
        return <TrendingDown style={{ color: '#f44336' }} />;
      default:
        return <TrendingFlat style={{ color: '#ff9800' }} />;
    }
  };

  const getTrendColor = (trend) => {
    switch (trend) {
      case 'UP':
        return '#4caf50';
      case 'DOWN':
        return '#f44336';
      default:
        return '#ff9800';
    }
  };

  // Generate sample trend data for charts
  const generateTrendData = () => {
    const data = [];
    const now = new Date();
    for (let i = 6; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);
      data.push({
        date: date.toLocaleDateString('ja-JP', { month: 'short', day: 'numeric' }),
        revenue: 2000 + Math.random() * 1000,
        orders: 40 + Math.random() * 20,
        customers: 15 + Math.random() * 10,
      });
    }
    return data;
  };

  const trendData = generateTrendData();

  if (loading && !dashboardData) {
    return (
      <Container maxWidth="lg" className={classes.root}>
        <Box display="flex" justifyContent="center" alignItems="center" height="400px">
          <CircularProgress size={60} />
          <Box ml={2}>
            <Typography variant="h6">ダッシュボードを読み込んでいます...</Typography>
          </Box>
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" className={classes.root}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={fetchDashboardData}>
            再試行
          </Button>
        }>
          <AlertTitle>エラーが発生しました</AlertTitle>
          {error}
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" className={classes.root}>
      <Breadcrumbs className={classes.breadcrumbs}>
        <Link color="inherit" href="/">
          ダッシュボード
        </Link>
        <Link color="inherit" href="/reports">
          レポート
        </Link>
        <Typography color="textPrimary">エグゼクティブダッシュボード</Typography>
      </Breadcrumbs>

      <Box className={classes.title}>
        <Box className={classes.titleText}>
          <Dashboard />
          <Typography variant="h4" component="h1">
            エグゼクティブダッシュボード
          </Typography>
        </Box>
        <IconButton
          color="primary"
          onClick={fetchDashboardData}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : <Refresh />}
        </IconButton>
      </Box>

      {dashboardData && (
        <>
          {/* Revenue KPIs */}
          <Typography variant="h6" gutterBottom style={{ marginTop: 24 }}>
            売上指標
          </Typography>
          <Grid container spacing={3} style={{ marginBottom: 24 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <AttachMoney className={classes.kpiIcon} style={{ color: '#4caf50' }} />
                <Typography className={classes.kpiValue} style={{ color: '#4caf50' }}>
                  {formatCurrency(dashboardData.revenue?.todayRevenue)}
                </Typography>
                <Typography className={classes.kpiLabel}>今日の売上</Typography>
                <Box className={classes.kpiTrend}>
                  <Chip
                    size="small"
                    label={formatPercentage(dashboardData.revenue?.revenueGrowth || 0)}
                    style={{
                      backgroundColor: getTrendColor('UP'),
                      color: 'white',
                    }}
                    className={classes.trendChip}
                  />
                </Box>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <AttachMoney className={classes.kpiIcon} style={{ color: '#2196f3' }} />
                <Typography className={classes.kpiValue} style={{ color: '#2196f3' }}>
                  {formatCurrency(dashboardData.revenue?.weekRevenue)}
                </Typography>
                <Typography className={classes.kpiLabel}>今週の売上</Typography>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <AttachMoney className={classes.kpiIcon} style={{ color: '#ff9800' }} />
                <Typography className={classes.kpiValue} style={{ color: '#ff9800' }}>
                  {formatCurrency(dashboardData.revenue?.monthRevenue)}
                </Typography>
                <Typography className={classes.kpiLabel}>今月の売上</Typography>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <AttachMoney className={classes.kpiIcon} style={{ color: '#9c27b0' }} />
                <Typography className={classes.kpiValue} style={{ color: '#9c27b0' }}>
                  {formatCurrency(dashboardData.revenue?.yearRevenue)}
                </Typography>
                <Typography className={classes.kpiLabel}>今年の売上</Typography>
              </Card>
            </Grid>
          </Grid>

          {/* Other KPIs */}
          <Grid container spacing={3} style={{ marginBottom: 24 }}>
            {/* Orders */}
            <Grid item xs={12} sm={4}>
              <Card className={classes.kpiCard}>
                <ShoppingCart className={classes.kpiIcon} style={{ color: '#4caf50' }} />
                <Typography className={classes.kpiValue} style={{ color: '#4caf50' }}>
                  {formatNumber(dashboardData.orders?.todayOrders)}
                </Typography>
                <Typography className={classes.kpiLabel}>今日の注文数</Typography>
                <Box className={classes.kpiTrend}>
                  {getTrendIcon('UP')}
                  <Typography variant="caption" style={{ color: getTrendColor('UP') }}>
                    {formatPercentage(dashboardData.orders?.orderGrowth || 0)}
                  </Typography>
                </Box>
              </Card>
            </Grid>

            {/* Customers */}
            <Grid item xs={12} sm={4}>
              <Card className={classes.kpiCard}>
                <People className={classes.kpiIcon} style={{ color: '#2196f3' }} />
                <Typography className={classes.kpiValue} style={{ color: '#2196f3' }}>
                  {formatNumber(dashboardData.customers?.totalCustomers)}
                </Typography>
                <Typography className={classes.kpiLabel}>総顧客数</Typography>
                <Box className={classes.kpiTrend}>
                  {getTrendIcon('UP')}
                  <Typography variant="caption" style={{ color: getTrendColor('UP') }}>
                    {formatPercentage(dashboardData.customers?.customerGrowth || 0)}
                  </Typography>
                </Box>
              </Card>
            </Grid>

            {/* Inventory */}
            <Grid item xs={12} sm={4}>
              <Card className={classes.kpiCard}>
                <Storage className={classes.kpiIcon} style={{ color: '#ff9800' }} />
                <Typography className={classes.kpiValue} style={{ color: '#ff9800' }}>
                  {formatNumber(dashboardData.inventory?.totalProducts)}
                </Typography>
                <Typography className={classes.kpiLabel}>商品数</Typography>
                <Typography variant="caption" color="error">
                  低在庫: {dashboardData.inventory?.lowStockItems}品目
                </Typography>
              </Card>
            </Grid>
          </Grid>

          {/* Trend Charts */}
          <Grid container spacing={3}>
            <Grid item xs={12} lg={6}>
              <Paper className={classes.chartCard}>
                <Typography variant="h6" className={classes.chartTitle}>
                  売上トレンド（7日間）
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <AreaChart data={trendData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => [formatCurrency(value), '売上']}
                    />
                    <Area
                      type="monotone"
                      dataKey="revenue"
                      stroke="#8884d8"
                      fill="#8884d8"
                      fillOpacity={0.6}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>

            <Grid item xs={12} lg={6}>
              <Paper className={classes.chartCard}>
                <Typography variant="h6" className={classes.chartTitle}>
                  注文数トレンド（7日間）
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={trendData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => [value, '注文数']}
                    />
                    <Line
                      type="monotone"
                      dataKey="orders"
                      stroke="#82ca9d"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>
          </Grid>

          {/* Key Metrics Summary */}
          <Card style={{ marginTop: 24 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                主要指標サマリー
              </Typography>
              <Grid container spacing={2}>
                {dashboardData.trends?.map((trend, index) => (
                  <Grid item xs={12} sm={6} md={3} key={index}>
                    <Box display="flex" alignItems="center" justifyContent="space-between">
                      <Typography variant="body2">
                        {trend.metric}
                      </Typography>
                      <Box display="flex" alignItems="center" gap={0.5}>
                        {getTrendIcon(trend.trend)}
                        <Typography
                          variant="body2"
                          style={{ color: getTrendColor(trend.trend) }}
                        >
                          {formatPercentage(trend.changePercent)}
                        </Typography>
                      </Box>
                    </Box>
                  </Grid>
                ))}
              </Grid>
              <Typography className={classes.lastUpdated}>
                最終更新: {lastUpdated.toLocaleString('ja-JP')}
              </Typography>
            </CardContent>
          </Card>
        </>
      )}
    </Container>
  );
};

export default DashboardReport;