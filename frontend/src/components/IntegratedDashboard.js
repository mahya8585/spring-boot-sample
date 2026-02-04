import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  Box,
  CircularProgress,
  Alert,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import {
  TrendingUp,
  TrendingDown,
  Assessment,
  Speed,
  Memory,
  Storage
} from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  card: {
    height: '100%',
  },
  metricCard: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(2),
  },
  metricIcon: {
    fontSize: 40,
    marginRight: theme.spacing(2),
    color: theme.palette.primary.main,
  },
  metricValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  trendIcon: {
    marginLeft: theme.spacing(1),
  },
  controls: {
    marginBottom: theme.spacing(3),
  },
  refreshButton: {
    marginLeft: theme.spacing(2),
  }
}));

function IntegratedDashboard() {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [metrics, setMetrics] = useState(null);
  const [category, setCategory] = useState('');
  const [autoRefresh, setAutoRefresh] = useState(true);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      const request = {
        category: category || null,
        includeRealTimeData: true,
        cacheStrategy: 'default'
      };

      // Fetch dashboard data
      const dashboardResponse = await fetch('/api/v1/inventory/integrated/realtime-dashboard', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request),
      });

      if (!dashboardResponse.ok) {
        throw new Error('Failed to fetch dashboard data');
      }

      const dashboardResult = await dashboardResponse.json();

      // Fetch performance metrics
      const metricsResponse = await fetch('/api/v1/inventory/integrated/metrics');
      
      if (!metricsResponse.ok) {
        throw new Error('Failed to fetch metrics');
      }

      const metricsResult = await metricsResponse.json();

      setDashboardData(dashboardResult);
      setMetrics(metricsResult);

    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('ダッシュボードデータの取得に失敗しました。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, [category]);

  useEffect(() => {
    if (autoRefresh) {
      const interval = setInterval(fetchDashboardData, 60000); // Refresh every minute
      return () => clearInterval(interval);
    }
  }, [autoRefresh, category]);

  const getTrendIcon = (trend) => {
    switch (trend) {
      case 'up':
        return <TrendingUp className={classes.trendIcon} style={{ color: '#4caf50' }} />;
      case 'down':
        return <TrendingDown className={classes.trendIcon} style={{ color: '#f44336' }} />;
      default:
        return null;
    }
  };

  if (loading && !dashboardData) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
        <Typography variant="h6" style={{ marginLeft: 16 }}>
          統合ダッシュボードを読み込み中...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" className={classes.root}>
        {error}
        <Button onClick={fetchDashboardData} style={{ marginLeft: 16 }}>
          再試行
        </Button>
      </Alert>
    );
  }

  return (
    <div className={classes.root}>
      <Typography variant="h4" gutterBottom>
        統合インベントリダッシュボード (Phase 4)
      </Typography>

      {/* Controls */}
      <Box className={classes.controls}>
        <Grid container spacing={2} alignItems="center">
          <Grid item>
            <FormControl variant="outlined" size="small" style={{ minWidth: 120 }}>
              <InputLabel>カテゴリ</InputLabel>
              <Select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                label="カテゴリ"
              >
                <MenuItem value="">すべて</MenuItem>
                <MenuItem value="JAVA">Java</MenuItem>
                <MenuItem value="JAVASCRIPT">JavaScript</MenuItem>
                <MenuItem value="PYTHON">Python</MenuItem>
                <MenuItem value="AI_ML">AI/ML</MenuItem>
                <MenuItem value="DATABASE">Database</MenuItem>
                <MenuItem value="CLOUD">Cloud</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item>
            <Button 
              variant="outlined" 
              onClick={fetchDashboardData}
              className={classes.refreshButton}
              disabled={loading}
            >
              更新
            </Button>
          </Grid>
        </Grid>
      </Box>

      {dashboardData && (
        <Grid container spacing={3}>
          {/* KPI Cards */}
          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.card}>
              <CardContent className={classes.metricCard}>
                <Storage className={classes.metricIcon} />
                <Box>
                  <Typography className={classes.metricValue}>
                    {dashboardData.kpis?.totalItems || 0}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    総アイテム数
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.card}>
              <CardContent className={classes.metricCard}>
                <Assessment className={classes.metricIcon} style={{ color: '#ff9800' }} />
                <Box>
                  <Typography className={classes.metricValue}>
                    ¥{dashboardData.kpis?.totalValue?.toLocaleString() || 0}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    総在庫価値
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.card}>
              <CardContent className={classes.metricCard}>
                <Memory className={classes.metricIcon} style={{ color: '#f44336' }} />
                <Box>
                  <Typography className={classes.metricValue}>
                    {dashboardData.kpis?.lowStockItems || 0}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    在庫不足
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.card}>
              <CardContent className={classes.metricCard}>
                <Speed className={classes.metricIcon} style={{ color: '#4caf50' }} />
                <Box>
                  <Typography className={classes.metricValue}>
                    {dashboardData.kpis?.outOfStockItems || 0}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    在庫切れ
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Performance Trends */}
          {dashboardData.trends && (
            <Grid item xs={12} md={6}>
              <Card className={classes.card}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    パフォーマンストレンド
                  </Typography>
                  {dashboardData.trends.map((trend, index) => (
                    <Box key={index} display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                      <Typography variant="body1">
                        {trend.metric === 'inventory_turnover' ? '在庫回転率' : 
                         trend.metric === 'stock_efficiency' ? '在庫効率' : trend.metric}
                      </Typography>
                      <Box display="flex" alignItems="center">
                        <Typography variant="h6" style={{ marginRight: 8 }}>
                          {trend.value}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          {trend.change}
                        </Typography>
                        {getTrendIcon(trend.trend)}
                      </Box>
                    </Box>
                  ))}
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* System Performance */}
          {metrics && (
            <Grid item xs={12} md={6}>
              <Card className={classes.card}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    システムパフォーマンス
                  </Typography>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="body1">平均応答時間</Typography>
                    <Typography variant="h6">{metrics.response_time_avg}</Typography>
                  </Box>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="body1">キャッシュヒット率</Typography>
                    <Typography variant="h6">{metrics.cache_hit_rate}</Typography>
                  </Box>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="body1">同時接続ユーザー</Typography>
                    <Typography variant="h6">{metrics.concurrent_users}</Typography>
                  </Box>
                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="body1">分析成功率</Typography>
                    <Typography variant="h6">{metrics.analysis_success_rate}</Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* System Health */}
          {dashboardData.systemHealth && (
            <Grid item xs={12}>
              <Card className={classes.card}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    システム状態
                  </Typography>
                  <Box display="flex" alignItems="center">
                    <Box 
                      width={12} 
                      height={12} 
                      borderRadius="50%" 
                      bgcolor={dashboardData.systemHealth.status === 'healthy' ? '#4caf50' : '#f44336'}
                      mr={1}
                    />
                    <Typography variant="body1">
                      ステータス: {dashboardData.systemHealth.status === 'healthy' ? '正常' : 'エラー'}
                    </Typography>
                    <Typography variant="body2" color="textSecondary" style={{ marginLeft: 16 }}>
                      最終更新: {new Date(dashboardData.systemHealth.lastUpdated).toLocaleTimeString()}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          )}
        </Grid>
      )}
    </div>
  );
}

export default IntegratedDashboard;