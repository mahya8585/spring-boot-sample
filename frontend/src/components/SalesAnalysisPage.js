import React, { useState, useEffect, useCallback } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Paper,
  makeStyles,
  Container,
  Breadcrumbs,
  Link,
  CircularProgress,
  Chip
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import {
  Assessment,
  FilterList,
  GetApp
} from '@material-ui/icons';
import { 
  ResponsiveContainer, 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { useHistory } from 'react-router-dom';
import { reports } from '../services/reportsApi';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  title: {
    marginBottom: theme.spacing(3),
    display: 'flex',
    alignItems: 'center',
    '& .MuiSvgIcon-root': {
      marginRight: theme.spacing(1),
      fontSize: '2rem',
    },
  },
  filterCard: {
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
  },
  filterSection: {
    display: 'flex',
    gap: theme.spacing(2),
    flexWrap: 'wrap',
    alignItems: 'flex-end',
  },
  chartCard: {
    height: 400,
    marginBottom: theme.spacing(3),
  },
  chartContent: {
    height: 350,
    padding: theme.spacing(2),
  },
  metricsCard: {
    padding: theme.spacing(2),
    textAlign: 'center',
    height: '100%',
  },
  metricValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  metricLabel: {
    color: theme.palette.text.secondary,
    marginTop: theme.spacing(1),
  },
  trendChip: {
    marginTop: theme.spacing(1),
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
  exportButton: {
    marginLeft: theme.spacing(2),
  },
}));

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

const SalesAnalysisPage = () => {
  const classes = useStyles();
  const history = useHistory();
  
  // State management
  const [salesAnalysis, setSalesAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Filter states
  const [startDate, setStartDate] = useState(() => {
    const date = new Date();
    date.setMonth(date.getMonth() - 3);
    return date.toISOString().split('T')[0];
  });
  const [endDate, setEndDate] = useState(() => {
    return new Date().toISOString().split('T')[0];
  });
  const [categoryCode, setCategoryCode] = useState('');
  const [customerSegment, setCustomerSegment] = useState('');

  // Load sales analysis data
  const loadSalesAnalysis = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      const data = await reports.getSalesAnalysis(
        startDate, 
        endDate, 
        categoryCode || null, 
        customerSegment || null
      );
      
      setSalesAnalysis(data);
    } catch (err) {
      setError(err.message);
      console.error('Failed to load sales analysis:', err);
    } finally {
      setLoading(false);
    }
  }, [startDate, endDate, categoryCode, customerSegment]);

  // Load data on component mount and filter changes
  useEffect(() => {
    loadSalesAnalysis();
  }, [loadSalesAnalysis]);

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY'
    }).format(value || 0);
  };

  // Format percentage
  const formatPercentage = (value) => {
    return `${(value || 0).toFixed(1)}%`;
  };

  // Handle export
  const handleExport = () => {
    // Mock export functionality
    console.log('Exporting sales analysis data...');
    alert('エクスポート機能は実装中です');
  };

  if (loading) {
    return (
      <Container className={classes.root}>
        <div className={classes.loadingContainer}>
          <CircularProgress />
        </div>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className={classes.root}>
        <Alert severity="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      </Container>
    );
  }

  return (
    <Container className={classes.root}>
      {/* Breadcrumbs */}
      <Breadcrumbs aria-label="breadcrumb" style={{ marginBottom: 24 }}>
        <Link color="inherit" onClick={() => history.push('/dashboard')}>
          ダッシュボード
        </Link>
        <Link color="inherit" onClick={() => history.push('/reports')}>
          レポート
        </Link>
        <Typography color="textPrimary">売上分析</Typography>
      </Breadcrumbs>

      {/* Page Title */}
      <Typography variant="h4" className={classes.title}>
        <Assessment />
        詳細売上分析
        <Button
          variant="outlined"
          startIcon={<GetApp />}
          className={classes.exportButton}
          onClick={handleExport}
        >
          エクスポート
        </Button>
      </Typography>

      {/* Filters */}
      <Card className={classes.filterCard}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            <FilterList style={{ verticalAlign: 'middle', marginRight: 8 }} />
            フィルター設定
          </Typography>
          <div className={classes.filterSection}>
            <TextField
              label="開始日"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              variant="outlined"
              size="small"
            />
            <TextField
              label="終了日"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              variant="outlined"
              size="small"
            />
            <FormControl variant="outlined" size="small" style={{ minWidth: 150 }}>
              <InputLabel>技術カテゴリ</InputLabel>
              <Select
                value={categoryCode}
                onChange={(e) => setCategoryCode(e.target.value)}
                label="技術カテゴリ"
              >
                <MenuItem value="">すべて</MenuItem>
                <MenuItem value="JAVA">Java</MenuItem>
                <MenuItem value="PYTHON">Python</MenuItem>
                <MenuItem value="JAVASCRIPT">JavaScript</MenuItem>
                <MenuItem value="REACT">React</MenuItem>
                <MenuItem value="SPRING">Spring</MenuItem>
              </Select>
            </FormControl>
            <FormControl variant="outlined" size="small" style={{ minWidth: 150 }}>
              <InputLabel>顧客セグメント</InputLabel>
              <Select
                value={customerSegment}
                onChange={(e) => setCustomerSegment(e.target.value)}
                label="顧客セグメント"
              >
                <MenuItem value="">すべて</MenuItem>
                <MenuItem value="BEGINNER">初心者</MenuItem>
                <MenuItem value="INTERMEDIATE">中級者</MenuItem>
                <MenuItem value="ADVANCED">上級者</MenuItem>
                <MenuItem value="ENTERPRISE">企業</MenuItem>
              </Select>
            </FormControl>
          </div>
        </CardContent>
      </Card>

      {salesAnalysis && (
        <>
          {/* Key Metrics */}
          <Grid container spacing={3} style={{ marginBottom: 24 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {formatCurrency(salesAnalysis.totalRevenue)}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    総売上
                  </Typography>
                  {salesAnalysis.growthRate && (
                    <Chip
                      label={`${formatPercentage(salesAnalysis.growthRate)} 成長`}
                      color="primary"
                      size="small"
                      className={classes.trendChip}
                    />
                  )}
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {salesAnalysis.totalOrders?.toLocaleString()}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    総注文数
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {formatCurrency(salesAnalysis.averageOrderValue)}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    平均注文額
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {salesAnalysis.techCategorySales?.length || 0}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    アクティブカテゴリ
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Period Sales Trend */}
          {salesAnalysis.periodSales && salesAnalysis.periodSales.length > 0 && (
            <Card className={classes.chartCard}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  期間別売上推移
                </Typography>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={salesAnalysis.periodSales}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis 
                      dataKey="periodDate" 
                      tickFormatter={(date) => new Date(date).toLocaleDateString('ja-JP')}
                    />
                    <YAxis tickFormatter={(value) => `¥${(value / 1000).toFixed(0)}K`} />
                    <Tooltip 
                      formatter={(value) => [formatCurrency(value), '売上']}
                      labelFormatter={(date) => new Date(date).toLocaleDateString('ja-JP')}
                    />
                    <Legend />
                    <Line 
                      type="monotone" 
                      dataKey="revenue" 
                      stroke="#8884d8" 
                      strokeWidth={2}
                      name="売上"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          )}

          {/* Tech Category Performance */}
          <Grid container spacing={3}>
            {salesAnalysis.techCategorySales && salesAnalysis.techCategorySales.length > 0 && (
              <Grid item xs={12} md={8}>
                <Card className={classes.chartCard}>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      技術カテゴリ別売上
                    </Typography>
                    <ResponsiveContainer width="100%" height={300}>
                      <BarChart data={salesAnalysis.techCategorySales}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="categoryName" />
                        <YAxis tickFormatter={(value) => `¥${(value / 1000).toFixed(0)}K`} />
                        <Tooltip formatter={(value) => [formatCurrency(value), '売上']} />
                        <Legend />
                        <Bar dataKey="revenue" fill="#8884d8" name="売上" />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </Grid>
            )}

            {/* Customer Segment Distribution */}
            {salesAnalysis.customerSegmentSales && salesAnalysis.customerSegmentSales.length > 0 && (
              <Grid item xs={12} md={4}>
                <Card className={classes.chartCard}>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      顧客セグメント別売上
                    </Typography>
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={salesAnalysis.customerSegmentSales}
                          cx="50%"
                          cy="50%"
                          labelLine={false}
                          label={({ segmentType, percent }) => `${segmentType} ${(percent * 100).toFixed(0)}%`}
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="revenue"
                        >
                          {salesAnalysis.customerSegmentSales.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip formatter={(value) => formatCurrency(value)} />
                      </PieChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </Grid>
            )}
          </Grid>

          {/* Profitability Analysis */}
          {salesAnalysis.profitabilityItems && salesAnalysis.profitabilityItems.length > 0 && (
            <Card style={{ marginTop: 24 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  収益性分析
                </Typography>
                <Grid container spacing={2}>
                  {salesAnalysis.profitabilityItems.map((item, index) => (
                    <Grid item xs={12} sm={6} md={4} key={index}>
                      <Paper style={{ padding: 16 }}>
                        <Typography variant="subtitle1" gutterBottom>
                          {item.itemName}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          売上: {formatCurrency(item.revenue)}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          利益: {formatCurrency(item.profit)}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          利益率: {formatPercentage(item.profitMargin)}
                        </Typography>
                      </Paper>
                    </Grid>
                  ))}
                </Grid>
              </CardContent>
            </Card>
          )}
        </>
      )}
    </Container>
  );
};

export default SalesAnalysisPage;