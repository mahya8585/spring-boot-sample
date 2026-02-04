import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Grid,
  Paper,
  CircularProgress,
  Snackbar,
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Tabs,
  Tab,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { Refresh, GetApp, Settings, TrendingUp } from '@material-ui/icons';
import KPICardGrid from './dashboard/KPICardGrid';
import TrendChartGrid from './dashboard/TrendChartGrid';
import AlertsPanel from './dashboard/AlertsPanel';
import CustomReportCreator from './CustomReportCreator';
import api from '../services/api';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  paper: {
    padding: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
  header: {
    marginBottom: theme.spacing(3),
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  refreshButton: {
    minWidth: 'auto',
    padding: theme.spacing(1),
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
  lastUpdated: {
    color: theme.palette.text.secondary,
    fontSize: '0.875rem',
    marginTop: theme.spacing(1),
  },
  actionButtons: {
    display: 'flex',
    gap: theme.spacing(1),
  },
  tabContent: {
    marginTop: theme.spacing(2),
  },
}));

const ExecutiveDashboard = () => {
  const classes = useStyles();
  const [dashboardData, setDashboardData] = useState(null);
  const [trendsData, setTrendsData] = useState(null);
  const [alertsData, setAlertsData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [drillDownDialog, setDrillDownDialog] = useState(false);
  const [drillDownData, setDrillDownData] = useState(null);
  const [activeTab, setActiveTab] = useState(0);
  const [customReportDialog, setCustomReportDialog] = useState(false);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch all dashboard data in parallel
      const [kpisResponse, trendsResponse, alertsResponse] = await Promise.all([
        api.get('/reports/dashboard/kpis'),
        api.get('/reports/dashboard/trends'),
        api.get('/reports/dashboard/alerts')
      ]);

      setDashboardData(kpisResponse.data);
      setTrendsData(trendsResponse.data);
      setAlertsData(alertsResponse.data);
      setLastUpdated(new Date());
    } catch (err) {
      console.error('Dashboard data fetch error:', err);
      setError('ダッシュボードデータの取得に失敗しました');
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

  const handleRefresh = () => {
    fetchDashboardData();
  };

  const handleCloseError = () => {
    setError(null);
  };

  const handleDrillDown = async (chartType, dataPoint) => {
    try {
      setLoading(true);
      
      // Fetch drill-down data based on chart type and data point
      const response = await api.post('/reports/drill-down', {
        reportType: chartType,
        drillDownDimension: dataPoint.dimension || 'tech_category',
        filters: dataPoint
      });

      setDrillDownData({
        chartType,
        dataPoint,
        data: response.data
      });
      setDrillDownDialog(true);
    } catch (err) {
      setError('ドリルダウンデータの取得に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  const handleExportDashboard = async (format) => {
    try {
      setLoading(true);
      
      // Create a dashboard export request
      const response = await api.post('/reports/custom-reports', {
        reportType: 'EXECUTIVE_DASHBOARD',
        reportName: `経営ダッシュボード_${new Date().toISOString().split('T')[0]}`,
        description: '経営ダッシュボードの完全エクスポート',
        createdBy: 'current_user'
      });

      if (response.data.reportId) {
        // Export the generated report
        const exportResponse = await api.get(`/reports/export/${response.data.reportId}?format=${format}`, {
          responseType: 'blob'
        });

        const blob = new Blob([exportResponse.data]);
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `executive_dashboard.${format.toLowerCase()}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }
    } catch (err) {
      setError('ダッシュボードのエクスポートに失敗しました');
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const renderDashboardView = () => (
    <Grid container spacing={3}>
      {/* KPI Cards */}
      <Grid item xs={12}>
        <KPICardGrid data={dashboardData} loading={loading} />
      </Grid>

      {/* Charts and Alerts */}
      <Grid item xs={12} md={8}>
        <TrendChartGrid 
          data={trendsData} 
          loading={loading} 
          onDrillDown={handleDrillDown}
        />
      </Grid>
      
      <Grid item xs={12} md={4}>
        <AlertsPanel alerts={alertsData} loading={loading} />
      </Grid>
    </Grid>
  );

  const renderAdvancedAnalytics = () => (
    <Box className={classes.tabContent}>
      <Grid container spacing={3}>
        {/* Enhanced Analytics Components */}
        <Grid item xs={12}>
          <Paper className={classes.paper}>
            <Typography variant="h6" gutterBottom>
              高度分析・予測機能
            </Typography>
            <Typography variant="body2" paragraph>
              ここでは技術トレンド予測、顧客行動分析、在庫最適化などの高度な分析機能を提供します。
            </Typography>
            <Button
              variant="contained"
              color="primary"
              startIcon={<TrendingUp />}
              onClick={() => setCustomReportDialog(true)}
            >
              カスタムレポート作成
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );

  if (loading && !dashboardData) {
    return (
      <Container maxWidth="xl" className={classes.root}>
        <div className={classes.loading}>
          <CircularProgress size={60} />
        </div>
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" className={classes.root}>
      <Paper className={classes.paper}>
        <Box className={classes.header}>
          <Typography variant="h4" component="h1" gutterBottom>
            技術専門書店 経営ダッシュボード
          </Typography>
          <Box className={classes.actionButtons}>
            {loading && <CircularProgress size={24} />}
            <Button
              variant="outlined"
              startIcon={<GetApp />}
              onClick={() => handleExportDashboard('PDF')}
              disabled={loading}
            >
              PDF出力
            </Button>
            <Button
              variant="outlined"
              startIcon={<GetApp />}
              onClick={() => handleExportDashboard('EXCEL')}
              disabled={loading}
            >
              Excel出力
            </Button>
            <Button
              variant="outlined"
              startIcon={<Settings />}
              onClick={() => setCustomReportDialog(true)}
            >
              カスタムレポート
            </Button>
            <Refresh 
              style={{ cursor: 'pointer' }} 
              onClick={handleRefresh}
              color={loading ? 'disabled' : 'primary'}
            />
          </Box>
        </Box>
        {lastUpdated && (
          <Typography className={classes.lastUpdated}>
            最終更新: {lastUpdated.toLocaleString('ja-JP')}
          </Typography>
        )}
      </Paper>

      {/* Main Dashboard Tabs */}
      <Paper style={{ marginBottom: 16 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
        >
          <Tab label="経営ダッシュボード" />
          <Tab label="高度分析" />
        </Tabs>
      </Paper>

      {activeTab === 0 && renderDashboardView()}
      {activeTab === 1 && renderAdvancedAnalytics()}

      {/* Drill-down Dialog */}
      <Dialog
        open={drillDownDialog}
        onClose={() => setDrillDownDialog(false)}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle>
          詳細分析 - {drillDownData?.chartType}
        </DialogTitle>
        <DialogContent>
          {drillDownData && (
            <Box>
              <Typography variant="h6" gutterBottom>
                {drillDownData.dataPoint?.name || 'データポイント'}の詳細
              </Typography>
              <pre style={{ whiteSpace: 'pre-wrap', fontSize: '12px' }}>
                {JSON.stringify(drillDownData.data, null, 2)}
              </pre>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={() => handleExportDashboard('PDF')}
            color="primary"
            startIcon={<GetApp />}
          >
            PDF出力
          </Button>
          <Button onClick={() => setDrillDownDialog(false)}>
            閉じる
          </Button>
        </DialogActions>
      </Dialog>

      {/* Custom Report Dialog */}
      <Dialog
        open={customReportDialog}
        onClose={() => setCustomReportDialog(false)}
        maxWidth="xl"
        fullWidth
        fullScreen
      >
        <DialogTitle>
          カスタムレポート作成
        </DialogTitle>
        <DialogContent>
          <CustomReportCreator />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCustomReportDialog(false)}>
            閉じる
          </Button>
        </DialogActions>
      </Dialog>

      {/* Error Snackbar */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={handleCloseError}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseError} severity="error">
          {error}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default ExecutiveDashboard;