import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Paper,
  makeStyles,
  Container,
  Breadcrumbs,
  Link,
  CircularProgress,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tab,
  Tabs,
  Badge
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import {
  Storage,
  Warning,
  Refresh,
  GetApp
} from '@material-ui/icons';
import { 
  ResponsiveContainer, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ScatterChart,
  Scatter
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
    justifyContent: 'space-between',
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
  warningMetric: {
    color: theme.palette.error.main,
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
  tabPanel: {
    paddingTop: theme.spacing(2),
  },
  tableContainer: {
    marginTop: theme.spacing(2),
    maxHeight: 400,
  },
  riskHigh: {
    color: theme.palette.error.main,
    fontWeight: 'bold',
  },
  riskMedium: {
    color: theme.palette.warning.main,
    fontWeight: 'bold',
  },
  riskLow: {
    color: theme.palette.success.main,
    fontWeight: 'bold',
  },
  statusChip: {
    fontWeight: 'bold',
  },
  recommendationCard: {
    backgroundColor: theme.palette.grey[50],
    margin: theme.spacing(1, 0),
    padding: theme.spacing(2),
  },
}));


function TabPanel({ children, value, index, ...other }) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`inventory-tabpanel-${index}`}
      aria-labelledby={`inventory-tab-${index}`}
      {...other}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

const InventoryAnalysisPage = () => {
  const classes = useStyles();
  const history = useHistory();
  
  // State management
  const [inventoryAnalysis, setInventoryAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [tabValue, setTabValue] = useState(0);
  
  // Filter states
  const [categoryCode, setCategoryCode] = useState('');
  const [analysisType, setAnalysisType] = useState('COMPREHENSIVE');

  // Load inventory analysis data
  const loadInventoryAnalysis = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      const data = await reports.getInventoryAnalysis(
        categoryCode || null, 
        analysisType
      );
      
      setInventoryAnalysis(data);
    } catch (err) {
      setError(err.message);
      console.error('Failed to load inventory analysis:', err);
    } finally {
      setLoading(false);
    }
  }, [categoryCode, analysisType]);

  // Load data on component mount and filter changes
  useEffect(() => {
    loadInventoryAnalysis();
  }, [loadInventoryAnalysis]);

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY'
    }).format(value || 0);
  };

  // Get status color
  const getStatusColor = (status) => {
    switch (status) {
      case 'CRITICAL': return 'error';
      case 'LOW': return 'warning';
      case 'OVERSTOCK': return 'secondary';
      default: return 'primary';
    }
  };

  // Get risk class
  const getRiskClass = (risk) => {
    switch (risk) {
      case 'HIGH': return classes.riskHigh;
      case 'MEDIUM': return classes.riskMedium;
      case 'LOW': return classes.riskLow;
      default: return '';
    }
  };

  // Handle export
  const handleExport = () => {
    console.log('Exporting inventory analysis data...');
    alert('エクスポート機能は実装中です');
  };

  // Handle refresh
  const handleRefresh = () => {
    loadInventoryAnalysis();
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
        <Typography color="textPrimary">在庫分析</Typography>
      </Breadcrumbs>

      {/* Page Title */}
      <div className={classes.title}>
        <Typography variant="h4" style={{ display: 'flex', alignItems: 'center' }}>
          <Storage />
          詳細在庫分析
        </Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={handleRefresh}
            style={{ marginRight: 8 }}
          >
            更新
          </Button>
          <Button
            variant="outlined"
            startIcon={<GetApp />}
            onClick={handleExport}
          >
            エクスポート
          </Button>
        </Box>
      </div>

      {/* Filters */}
      <Card className={classes.filterCard}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            分析設定
          </Typography>
          <div className={classes.filterSection}>
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
              <InputLabel>分析タイプ</InputLabel>
              <Select
                value={analysisType}
                onChange={(e) => setAnalysisType(e.target.value)}
                label="分析タイプ"
              >
                <MenuItem value="COMPREHENSIVE">総合分析</MenuItem>
                <MenuItem value="TURNOVER">回転率分析</MenuItem>
                <MenuItem value="DEAD_STOCK">デッドストック分析</MenuItem>
                <MenuItem value="OBSOLESCENCE">陳腐化リスク分析</MenuItem>
              </Select>
            </FormControl>
          </div>
        </CardContent>
      </Card>

      {inventoryAnalysis && (
        <>
          {/* Key Metrics */}
          <Grid container spacing={3} style={{ marginBottom: 24 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {inventoryAnalysis.totalItems?.toLocaleString()}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    総在庫アイテム数
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {formatCurrency(inventoryAnalysis.totalInventoryValue)}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    総在庫価値
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={classes.metricValue}>
                    {inventoryAnalysis.averageInventoryTurnover?.toFixed(1)}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    平均在庫回転率
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card className={classes.metricsCard}>
                <CardContent>
                  <Typography className={`${classes.metricValue} ${classes.warningMetric}`}>
                    {inventoryAnalysis.deadStockItems?.toLocaleString()}
                  </Typography>
                  <Typography className={classes.metricLabel}>
                    デッドストック
                  </Typography>
                  <Typography variant="body2" color="error">
                    {formatCurrency(inventoryAnalysis.deadStockValue)}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Analysis Tabs */}
          <Card>
            <Tabs
              value={tabValue}
              onChange={(e, newValue) => setTabValue(newValue)}
              aria-label="inventory analysis tabs"
              variant="scrollable"
              scrollButtons="auto"
            >
              <Tab label="在庫回転率分析" />
              <Tab 
                label={
                  <Badge badgeContent={inventoryAnalysis.deadStockItems} color="error">
                    デッドストック分析
                  </Badge>
                } 
              />
              <Tab label="陳腐化リスク分析" />
              <Tab label="在庫レベル分析" />
            </Tabs>

            {/* Turnover Analysis Tab */}
            <TabPanel value={tabValue} index={0} className={classes.tabPanel}>
              {inventoryAnalysis.turnoverAnalysis && inventoryAnalysis.turnoverAnalysis.length > 0 && (
                <>
                  <Typography variant="h6" gutterBottom>
                    在庫回転率分析
                  </Typography>
                  <ResponsiveContainer width="100%" height={300}>
                    <ScatterChart data={inventoryAnalysis.turnoverAnalysis}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis 
                        dataKey="daysSinceLastSale" 
                        name="最終販売からの日数"
                        label={{ value: '最終販売からの日数', position: 'insideBottom', offset: -5 }}
                      />
                      <YAxis 
                        dataKey="turnoverRate" 
                        name="回転率"
                        label={{ value: '回転率', angle: -90, position: 'insideLeft' }}
                      />
                      <Tooltip 
                        cursor={{ strokeDasharray: '3 3' }}
                        content={({ active, payload }) => {
                          if (active && payload && payload.length) {
                            const data = payload[0].payload;
                            return (
                              <Paper style={{ padding: 8 }}>
                                <Typography variant="subtitle2">{data.bookTitle}</Typography>
                                <Typography variant="body2">回転率: {data.turnoverRate}</Typography>
                                <Typography variant="body2">在庫数: {data.currentStock}</Typography>
                                <Typography variant="body2">最終販売: {data.daysSinceLastSale}日前</Typography>
                              </Paper>
                            );
                          }
                          return null;
                        }}
                      />
                      <Scatter dataKey="turnoverRate" fill="#8884d8" />
                    </ScatterChart>
                  </ResponsiveContainer>
                  
                  <TableContainer className={classes.tableContainer}>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>書籍名</TableCell>
                          <TableCell>カテゴリ</TableCell>
                          <TableCell align="right">在庫数</TableCell>
                          <TableCell align="right">回転率</TableCell>
                          <TableCell>回転分類</TableCell>
                          <TableCell align="right">最終販売</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {inventoryAnalysis.turnoverAnalysis.map((item, index) => (
                          <TableRow key={index}>
                            <TableCell>{item.bookTitle}</TableCell>
                            <TableCell>{item.categoryCode}</TableCell>
                            <TableCell align="right">{item.currentStock}</TableCell>
                            <TableCell align="right">{item.turnoverRate?.toFixed(1)}</TableCell>
                            <TableCell>
                              <Chip 
                                label={item.turnoverCategory} 
                                size="small"
                                color={item.turnoverCategory === 'FAST' ? 'primary' : 
                                       item.turnoverCategory === 'DEAD' ? 'secondary' : 'default'}
                              />
                            </TableCell>
                            <TableCell align="right">{item.daysSinceLastSale}日前</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </>
              )}
            </TabPanel>

            {/* Dead Stock Analysis Tab */}
            <TabPanel value={tabValue} index={1} className={classes.tabPanel}>
              {inventoryAnalysis.deadStockAnalysis && inventoryAnalysis.deadStockAnalysis.length > 0 && (
                <>
                  <Typography variant="h6" gutterBottom>
                    <Warning style={{ verticalAlign: 'middle', marginRight: 8 }} />
                    デッドストック分析
                  </Typography>
                  
                  <TableContainer className={classes.tableContainer}>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>書籍名</TableCell>
                          <TableCell>カテゴリ</TableCell>
                          <TableCell align="right">在庫数</TableCell>
                          <TableCell align="right">在庫価値</TableCell>
                          <TableCell align="right">未販売期間</TableCell>
                          <TableCell>リスクレベル</TableCell>
                          <TableCell>推奨アクション</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {inventoryAnalysis.deadStockAnalysis.map((item, index) => (
                          <TableRow key={index}>
                            <TableCell>{item.bookTitle}</TableCell>
                            <TableCell>{item.categoryCode}</TableCell>
                            <TableCell align="right">{item.currentStock}</TableCell>
                            <TableCell align="right">{formatCurrency(item.stockValue)}</TableCell>
                            <TableCell align="right">{item.daysSinceLastSale}日</TableCell>
                            <TableCell>
                              <span className={getRiskClass(item.riskLevel)}>
                                {item.riskLevel}
                              </span>
                            </TableCell>
                            <TableCell>{item.recommendedAction}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </>
              )}
            </TabPanel>

            {/* Obsolescence Risk Tab */}
            <TabPanel value={tabValue} index={2} className={classes.tabPanel}>
              {inventoryAnalysis.obsolescenceRisk && inventoryAnalysis.obsolescenceRisk.length > 0 && (
                <>
                  <Typography variant="h6" gutterBottom>
                    技術陳腐化リスク分析
                  </Typography>
                  
                  <Grid container spacing={3}>
                    {inventoryAnalysis.obsolescenceRisk.map((category, index) => (
                      <Grid item xs={12} md={6} key={index}>
                        <Paper className={classes.recommendationCard}>
                          <Typography variant="subtitle1" gutterBottom>
                            {category.categoryName}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            アイテム数: {category.itemCount}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            総価値: {formatCurrency(category.totalValue)}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            ライフサイクル: {category.techLifecycleStage}
                          </Typography>
                          <Typography variant="body2" className={getRiskClass(category.obsolescenceRisk)}>
                            陳腐化リスク: {category.obsolescenceRisk}
                          </Typography>
                          {category.monthsToObsolescence && (
                            <Typography variant="body2" color="error">
                              予想陳腐化まで: {category.monthsToObsolescence}ヶ月
                            </Typography>
                          )}
                          {category.mitigationStrategy && (
                            <Typography variant="body2" style={{ marginTop: 8 }}>
                              対策: {category.mitigationStrategy}
                            </Typography>
                          )}
                        </Paper>
                      </Grid>
                    ))}
                  </Grid>
                </>
              )}
            </TabPanel>

            {/* Stock Level Analysis Tab */}
            <TabPanel value={tabValue} index={3} className={classes.tabPanel}>
              {inventoryAnalysis.stockLevelAnalysis && inventoryAnalysis.stockLevelAnalysis.length > 0 && (
                <>
                  <Typography variant="h6" gutterBottom>
                    在庫レベル分析
                  </Typography>
                  
                  <TableContainer className={classes.tableContainer}>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>書籍名</TableCell>
                          <TableCell>カテゴリ</TableCell>
                          <TableCell align="right">現在在庫</TableCell>
                          <TableCell align="right">発注点</TableCell>
                          <TableCell align="right">発注量</TableCell>
                          <TableCell>ステータス</TableCell>
                          <TableCell align="right">在庫日数</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {inventoryAnalysis.stockLevelAnalysis.map((item, index) => (
                          <TableRow key={index}>
                            <TableCell>{item.bookTitle}</TableCell>
                            <TableCell>{item.categoryCode}</TableCell>
                            <TableCell align="right">{item.currentStock}</TableCell>
                            <TableCell align="right">{item.reorderPoint}</TableCell>
                            <TableCell align="right">{item.reorderQuantity}</TableCell>
                            <TableCell>
                              <Chip 
                                label={item.stockStatus} 
                                size="small"
                                color={getStatusColor(item.stockStatus)}
                                className={classes.statusChip}
                              />
                            </TableCell>
                            <TableCell align="right">{item.daysOfSupply}日</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </>
              )}
            </TabPanel>
          </Card>

          {/* Seasonal Trend Recommendations */}
          {inventoryAnalysis.seasonalTrend && (
            <Card style={{ marginTop: 24 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  季節性分析と推奨事項
                </Typography>
                <Paper className={classes.recommendationCard}>
                  <Typography variant="subtitle1">
                    現在の季節: {inventoryAnalysis.seasonalTrend.season}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    予想需要増加: {inventoryAnalysis.seasonalTrend.expectedDemandIncrease?.toFixed(1)}倍
                  </Typography>
                  {inventoryAnalysis.seasonalTrend.topCategories && (
                    <Typography variant="body2" color="textSecondary">
                      注目カテゴリ: {inventoryAnalysis.seasonalTrend.topCategories.join(', ')}
                    </Typography>
                  )}
                  {inventoryAnalysis.seasonalTrend.recommendation && (
                    <Typography variant="body2" style={{ marginTop: 8 }}>
                      推奨事項: {inventoryAnalysis.seasonalTrend.recommendation}
                    </Typography>
                  )}
                </Paper>
              </CardContent>
            </Card>
          )}
        </>
      )}
    </Container>
  );
};

export default InventoryAnalysisPage;