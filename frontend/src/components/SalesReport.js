import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  CircularProgress,
  Container,
  Breadcrumbs,
  Link,
  makeStyles,
  Paper,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@material-ui/core';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { TrendingUp, GetApp } from '@material-ui/icons';
import reports from '../services/reportsApi';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  title: {
    marginBottom: theme.spacing(3),
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(1),
  },
  filterCard: {
    marginBottom: theme.spacing(3),
  },
  chartCard: {
    padding: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
  chartTitle: {
    marginBottom: theme.spacing(2),
    fontWeight: 600,
  },
  kpiCard: {
    textAlign: 'center',
    padding: theme.spacing(2),
  },
  kpiValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  kpiLabel: {
    color: theme.palette.text.secondary,
    fontSize: '0.875rem',
  },
  loadingBox: {
    display: 'flex',
    justifyContent: 'center',
    padding: theme.spacing(4),
  },
  breadcrumbs: {
    marginBottom: theme.spacing(2),
  },
  tableCard: {
    marginTop: theme.spacing(3),
  },
  rankChip: {
    minWidth: 30,
    height: 24,
  },
}));

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

const SalesReport = () => {
  const classes = useStyles();
  const [loading, setLoading] = useState(false);
  const [reportData, setReportData] = useState(null);
  const [startDate, setStartDate] = useState(
    new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [endDate, setEndDate] = useState(
    new Date().toISOString().split('T')[0]
  );

  const fetchSalesReport = useCallback(async () => {
    setLoading(true);
    try {
      const response = await reports.getSalesReport(startDate, endDate);
      setReportData(response.data);
    } catch (error) {
      console.error('Error fetching sales report:', error);
    } finally {
      setLoading(false);
    }
  }, [startDate, endDate]);

  useEffect(() => {
    fetchSalesReport();
  }, [fetchSalesReport]);

  const handleDateChange = () => {
    fetchSalesReport();
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
    }).format(value);
  };

  const formatNumber = (value) => {
    return new Intl.NumberFormat('ja-JP').format(value);
  };

  // Prepare chart data
  const trendData = reportData?.trends?.map(item => ({
    date: new Date(item.date).toLocaleDateString('ja-JP', { month: 'short', day: 'numeric' }),
    revenue: item.revenue,
    orders: item.orderCount,
  })) || [];

  const breakdownData = reportData?.breakdown ? [
    { name: 'オンライン', value: reportData.breakdown.onlineRevenue, color: COLORS[0] },
    { name: '店頭', value: reportData.breakdown.walkInRevenue, color: COLORS[1] },
    { name: '電話', value: reportData.breakdown.phoneRevenue, color: COLORS[2] },
  ] : [];

  const rankingData = reportData?.rankings?.slice(0, 5).map(item => ({
    name: item.itemName,
    revenue: item.revenue,
    quantity: item.quantity,
  })) || [];

  if (loading && !reportData) {
    return (
      <Container maxWidth="lg" className={classes.root}>
        <Box className={classes.loadingBox}>
          <CircularProgress />
        </Box>
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
        <Typography color="textPrimary">売上レポート</Typography>
      </Breadcrumbs>

      <Typography variant="h4" component="h1" className={classes.title}>
        <TrendingUp />
        売上レポート
      </Typography>

      {/* Date Filter */}
      <Card className={classes.filterCard}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item>
              <TextField
                label="開始日"
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                InputLabelProps={{
                  shrink: true,
                }}
                variant="outlined"
                size="small"
              />
            </Grid>
            <Grid item>
              <TextField
                label="終了日"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                InputLabelProps={{
                  shrink: true,
                }}
                variant="outlined"
                size="small"
              />
            </Grid>
            <Grid item>
              <Button
                variant="contained"
                color="primary"
                onClick={handleDateChange}
                disabled={loading}
              >
                {loading ? <CircularProgress size={20} /> : '更新'}
              </Button>
            </Grid>
            <Grid item xs />
            <Grid item>
              <Button
                variant="outlined"
                startIcon={<GetApp />}
                onClick={() => console.log('Export sales report')}
              >
                エクスポート
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {reportData && (
        <>
          {/* KPI Cards */}
          <Grid container spacing={3} style={{ marginBottom: 24 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <Typography className={classes.kpiValue}>
                  {formatCurrency(reportData.totalRevenue)}
                </Typography>
                <Typography className={classes.kpiLabel}>
                  総売上
                </Typography>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <Typography className={classes.kpiValue}>
                  {formatNumber(reportData.totalOrders)}
                </Typography>
                <Typography className={classes.kpiLabel}>
                  注文数
                </Typography>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <Typography className={classes.kpiValue}>
                  {formatCurrency(reportData.averageOrderValue)}
                </Typography>
                <Typography className={classes.kpiLabel}>
                  平均注文金額
                </Typography>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.kpiCard}>
                <Typography className={classes.kpiValue} style={{ color: '#4caf50' }}>
                  +12.5%
                </Typography>
                <Typography className={classes.kpiLabel}>
                  前月比成長率
                </Typography>
              </Card>
            </Grid>
          </Grid>

          {/* Charts */}
          <Grid container spacing={3}>
            {/* Sales Trend Chart */}
            <Grid item xs={12} lg={8}>
              <Paper className={classes.chartCard}>
                <Typography variant="h6" className={classes.chartTitle}>
                  売上トレンド
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={trendData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis yAxisId="left" />
                    <YAxis yAxisId="right" orientation="right" />
                    <Tooltip
                      formatter={(value, name) => [
                        name === 'revenue' ? formatCurrency(value) : value,
                        name === 'revenue' ? '売上' : '注文数'
                      ]}
                    />
                    <Legend />
                    <Bar yAxisId="right" dataKey="orders" fill="#82ca9d" name="注文数" />
                    <Line
                      yAxisId="left"
                      type="monotone"
                      dataKey="revenue"
                      stroke="#8884d8"
                      strokeWidth={2}
                      name="売上"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>

            {/* Sales Breakdown Pie Chart */}
            <Grid item xs={12} lg={4}>
              <Paper className={classes.chartCard}>
                <Typography variant="h6" className={classes.chartTitle}>
                  販売チャネル別内訳
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={breakdownData}
                      cx="50%"
                      cy="50%"
                      outerRadius={80}
                      dataKey="value"
                      label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                    >
                      {breakdownData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => formatCurrency(value)} />
                  </PieChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>

            {/* Top Products Bar Chart */}
            <Grid item xs={12}>
              <Paper className={classes.chartCard}>
                <Typography variant="h6" className={classes.chartTitle}>
                  商品別売上ランキング（Top 5）
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={rankingData} layout="horizontal">
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis type="number" />
                    <YAxis dataKey="name" type="category" width={200} />
                    <Tooltip formatter={(value) => formatCurrency(value)} />
                    <Bar dataKey="revenue" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>
              </Paper>
            </Grid>
          </Grid>

          {/* Ranking Table */}
          <Card className={classes.tableCard}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                詳細ランキング
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>順位</TableCell>
                      <TableCell>商品名</TableCell>
                      <TableCell>カテゴリ</TableCell>
                      <TableCell align="right">売上金額</TableCell>
                      <TableCell align="right">販売数量</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {reportData.rankings?.map((item) => (
                      <TableRow key={item.rank}>
                        <TableCell>
                          <Chip
                            label={item.rank}
                            color={item.rank <= 3 ? 'primary' : 'default'}
                            size="small"
                            className={classes.rankChip}
                          />
                        </TableCell>
                        <TableCell>{item.itemName}</TableCell>
                        <TableCell>{item.category}</TableCell>
                        <TableCell align="right">
                          {formatCurrency(item.revenue)}
                        </TableCell>
                        <TableCell align="right">
                          {formatNumber(item.quantity)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </>
      )}
    </Container>
  );
};

export default SalesReport;