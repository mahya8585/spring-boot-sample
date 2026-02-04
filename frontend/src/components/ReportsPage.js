import React, { useCallback } from 'react';
import PropTypes from 'prop-types';
import {
  Box,
  Grid,
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  makeStyles,
  Container,
  Breadcrumbs,
  Link
} from '@material-ui/core';
import {
  TrendingUp,
  Storage,
  People,
  Assessment,
  Dashboard as DashboardIcon
} from '@material-ui/icons';
import { useHistory } from 'react-router-dom';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  title: {
    marginBottom: theme.spacing(3),
  },
  card: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    transition: 'transform 0.2s ease-in-out',
    '&:hover': {
      transform: 'translateY(-4px)',
      boxShadow: theme.shadows[8],
    },
  },
  cardContent: {
    flexGrow: 1,
  },
  cardIcon: {
    fontSize: 48,
    color: theme.palette.primary.main,
    marginBottom: theme.spacing(2),
  },
  cardTitle: {
    fontWeight: 600,
    marginBottom: theme.spacing(1),
  },
  cardDescription: {
    color: theme.palette.text.secondary,
  },
  cardActions: {
    padding: theme.spacing(2),
    paddingTop: 0,
  },
  breadcrumbs: {
    marginBottom: theme.spacing(2),
  },
}));

// Memoized ReportCard component for better performance
const ReportCard = React.memo(({ reportType, onReportClick, t }) => {
  const classes = useStyles();
  
  return (
    <Grid item xs={12} sm={6} md={4}>
      <Card className={classes.card}>
        <CardContent className={classes.cardContent}>
          <Box display="flex" justifyContent="center">
            {reportType.icon}
          </Box>
          <Typography 
            variant="h6" 
            component="h2" 
            className={classes.cardTitle}
            align="center"
          >
            {reportType.title}
          </Typography>
          <Typography 
            variant="body2" 
            className={classes.cardDescription}
            align="center"
          >
            {reportType.description}
          </Typography>
        </CardContent>
        <CardActions className={classes.cardActions}>
          <Button
            size="large"
            color="primary"
            variant="contained"
            fullWidth
            onClick={() => onReportClick(reportType)}
            style={{ backgroundColor: reportType.color }}
          >
            {t('report.show.button', 'レポートを表示')}
          </Button>
        </CardActions>
      </Card>
    </Grid>
  );
});

ReportCard.propTypes = {
  reportType: PropTypes.shape({
    id: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    icon: PropTypes.element.isRequired,
    path: PropTypes.string.isRequired,
    color: PropTypes.string.isRequired,
  }).isRequired,
  onReportClick: PropTypes.func.isRequired,
  t: PropTypes.func.isRequired,
};

const ReportsPage = () => {
  const classes = useStyles();
  const history = useHistory();
  const { t } = useI18n();

  const reportTypes = [
    {
      id: 'sales',
      title: t('report.sales.title', '売上レポート'),
      description: t('report.sales.description', '売上実績、トレンド分析、商品ランキングなどの売上関連データを確認できます。'),
      icon: <TrendingUp className={classes.cardIcon} />,
      path: '/reports/sales',
      color: '#4caf50',
    },
    {
      id: 'sales-analysis',
      title: t('report.sales.analysis.title', '詳細売上分析'),
      description: t('report.sales.analysis.description', '技術カテゴリ別、顧客セグメント別の多次元売上分析と収益性分析を行えます。'),
      icon: <Assessment className={classes.cardIcon} />,
      path: '/reports/sales/analysis',
      color: '#1976d2',
    },
    {
      id: 'inventory',
      title: t('report.inventory.title', '在庫レポート'),
      description: t('report.inventory.description', '在庫状況、発注提案、在庫回転率などの在庫管理データを分析できます。'),
      icon: <Storage className={classes.cardIcon} />,
      path: '/reports/inventory',
      color: '#ff9800',
    },
    {
      id: 'inventory-analysis',
      title: t('report.inventory.analysis.title', '詳細在庫分析'),
      description: t('report.inventory.analysis.description', '在庫回転率、デッドストック、技術陳腐化リスクの高度な分析を提供します。'),
      icon: <Storage className={classes.cardIcon} />,
      path: '/reports/inventory/analysis',
      color: '#f57c00',
    },
    {
      id: 'customers',
      title: t('report.customers.title', '顧客分析レポート'),
      description: t('report.customers.description', 'RFM分析、顧客セグメント、顧客価値分析などの顧客データを分析できます。'),
      icon: <People className={classes.cardIcon} />,
      path: '/reports/customers',
      color: '#2196f3',
    },
    {
      id: 'tech-trends',
      title: t('report.tech.trends.title', '技術トレンド分析'),
      description: t('report.tech.trends.description', '技術カテゴリ別のトレンド、人気度分析、技術スキル分布を確認できます。'),
      icon: <Assessment className={classes.cardIcon} />,
      path: '/reports/tech-trends',
      color: '#9c27b0',
    },
    {
      id: 'dashboard',
      title: t('report.dashboard.title', 'エグゼクティブダッシュボード'),
      description: t('report.dashboard.description', '経営KPI、リアルタイム指標、トレンドサマリーを一覧で確認できます。'),
      icon: <DashboardIcon className={classes.cardIcon} />,
      path: '/reports/dashboard',
      color: '#f44336',
    },
  ];

  const handleReportClick = useCallback((reportType) => {
    history.push(reportType.path);
  }, [history]);

  return (
    <Container maxWidth="lg" className={classes.root}>
      <Breadcrumbs className={classes.breadcrumbs}>
        <Link color="inherit" href="/">
          {t('breadcrumb.dashboard', 'ダッシュボード')}
        </Link>
        <Typography color="textPrimary">{t('breadcrumb.reports', 'レポート')}</Typography>
      </Breadcrumbs>

      <Typography variant="h4" component="h1" className={classes.title}>
        {t('report.title', 'レポート・分析')}
      </Typography>

      <Typography variant="body1" color="textSecondary" paragraph>
        {t('report.description', 'ビジネスインサイトを得るための包括的なレポート機能です。各レポートから詳細な分析データとビジュアライゼーションを確認できます。')}
      </Typography>

      <Grid container spacing={3}>
        {reportTypes.map((reportType) => (
          <ReportCard
            key={reportType.id}
            reportType={reportType}
            onReportClick={handleReportClick}
            t={t}
          />
        ))}
      </Grid>

      <Box mt={4}>
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {t('report.usage.title', 'ご利用について')}
            </Typography>
            <Typography variant="body2" color="textSecondary" paragraph>
              • {t('report.usage.update', 'レポートデータは定期的に更新されます（リアルタイム～日次更新）')}
            </Typography>
            <Typography variant="body2" color="textSecondary" paragraph>
              • {t('report.usage.processing', '大量データの処理には時間がかかる場合があります')}
            </Typography>
            <Typography variant="body2" color="textSecondary" paragraph>
              • {t('report.usage.export', 'データエクスポート機能は各レポートページから利用できます')}
            </Typography>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default React.memo(ReportsPage);