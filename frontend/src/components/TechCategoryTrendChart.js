import React from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardContent,
  Typography,
  makeStyles,
  Chip,
  Box
} from '@material-ui/core';
import {
  ResponsiveContainer,
  ComposedChart,
  Line,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  Area,
  AreaChart
} from 'recharts';
import { TrendingUp, TrendingDown, TrendingFlat } from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  root: {
    height: '100%',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing(2),
  },
  trendChip: {
    fontWeight: 'bold',
  },
  chartContainer: {
    height: 350,
  },
  metricBox: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: theme.spacing(1),
    margin: theme.spacing(0, 1),
    borderRadius: theme.shape.borderRadius,
    backgroundColor: theme.palette.grey[100],
  },
  metricValue: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  metricLabel: {
    fontSize: '0.75rem',
    color: theme.palette.text.secondary,
  },
  metricsRow: {
    display: 'flex',
    justifyContent: 'space-around',
    marginBottom: theme.spacing(2),
  },
}));

const TechCategoryTrendChart = ({ 
  categoryAnalysis, 
  chartType = 'composed',
  showMetrics = true,
  height = 350 
}) => {
  const classes = useStyles();

  if (!categoryAnalysis) {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6">技術カテゴリトレンド</Typography>
          <Typography color="textSecondary">データがありません</Typography>
        </CardContent>
      </Card>
    );
  }

  // Get trend icon and color
  const getTrendIcon = (direction) => {
    switch (direction) {
      case 'RISING': return <TrendingUp style={{ color: '#4caf50' }} />;
      case 'DECLINING': return <TrendingDown style={{ color: '#f44336' }} />;
      default: return <TrendingFlat style={{ color: '#ff9800' }} />;
    }
  };

  const getTrendColor = (direction) => {
    switch (direction) {
      case 'RISING': return 'primary';
      case 'DECLINING': return 'secondary';
      default: return 'default';
    }
  };

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
      notation: 'compact'
    }).format(value || 0);
  };

  // Prepare chart data from subcategories
  const chartData = categoryAnalysis.subCategories || [];

  // Custom tooltip for the chart
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <Card style={{ padding: 8 }}>
          <Typography variant="subtitle2">{label}</Typography>
          {payload.map((entry, index) => (
            <Typography key={index} variant="body2" style={{ color: entry.color }}>
              {entry.name}: {entry.name === '売上' ? formatCurrency(entry.value) : entry.value}
            </Typography>
          ))}
        </Card>
      );
    }
    return null;
  };

  return (
    <Card className={classes.root}>
      <CardContent>
        <div className={classes.header}>
          <Typography variant="h6">
            技術カテゴリトレンド: {categoryAnalysis.categoryName}
          </Typography>
          {categoryAnalysis.trend && (
            <Box display="flex" alignItems="center">
              {getTrendIcon(categoryAnalysis.trend.trendDirection)}
              <Chip
                label={`${categoryAnalysis.trend.growthRate?.toFixed(1)}% 成長`}
                color={getTrendColor(categoryAnalysis.trend.trendDirection)}
                size="small"
                className={classes.trendChip}
                style={{ marginLeft: 8 }}
              />
            </Box>
          )}
        </div>

        {/* Key Metrics */}
        {showMetrics && categoryAnalysis.metrics && (
          <div className={classes.metricsRow}>
            <div className={classes.metricBox}>
              <Typography className={classes.metricValue}>
                {categoryAnalysis.metrics.totalBooks}
              </Typography>
              <Typography className={classes.metricLabel}>
                書籍数
              </Typography>
            </div>
            <div className={classes.metricBox}>
              <Typography className={classes.metricValue}>
                {formatCurrency(categoryAnalysis.metrics.totalRevenue)}
              </Typography>
              <Typography className={classes.metricLabel}>
                売上
              </Typography>
            </div>
            <div className={classes.metricBox}>
              <Typography className={classes.metricValue}>
                {categoryAnalysis.metrics.marketShare?.toFixed(1)}%
              </Typography>
              <Typography className={classes.metricLabel}>
                市場シェア
              </Typography>
            </div>
            <div className={classes.metricBox}>
              <Typography className={classes.metricValue}>
                {categoryAnalysis.metrics.inventoryTurnover?.toFixed(1)}
              </Typography>
              <Typography className={classes.metricLabel}>
                回転率
              </Typography>
            </div>
          </div>
        )}

        {/* Chart */}
        <div className={classes.chartContainer}>
          <ResponsiveContainer width="100%" height={height}>
            {chartType === 'area' ? (
              <AreaChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="subCategoryName" />
                <YAxis yAxisId="left" />
                <YAxis yAxisId="right" orientation="right" />
                <Tooltip content={<CustomTooltip />} />
                <Legend />
                <Area
                  yAxisId="left"
                  type="monotone"
                  dataKey="revenue"
                  stackId="1"
                  stroke="#8884d8"
                  fill="#8884d8"
                  name="売上"
                />
              </AreaChart>
            ) : (
              <ComposedChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="subCategoryName" />
                <YAxis yAxisId="left" />
                <YAxis yAxisId="right" orientation="right" />
                <Tooltip content={<CustomTooltip />} />
                <Legend />
                <Bar 
                  yAxisId="left" 
                  dataKey="revenue" 
                  fill="#8884d8" 
                  name="売上"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="marketShare"
                  stroke="#ff7300"
                  strokeWidth={2}
                  name="市場シェア (%)"
                />
              </ComposedChart>
            )}
          </ResponsiveContainer>
        </div>

        {/* Trend Analysis */}
        {categoryAnalysis.trend && (
          <Box mt={2}>
            <Typography variant="subtitle2" gutterBottom>
              トレンド分析
            </Typography>
            <Typography variant="body2" color="textSecondary">
              {categoryAnalysis.trend.trendAnalysis || 
               `この技術カテゴリは${categoryAnalysis.trend.trendDirection === 'RISING' ? '上昇' : 
                 categoryAnalysis.trend.trendDirection === 'DECLINING' ? '下降' : '安定'}トレンドにあります。`}
            </Typography>
            {categoryAnalysis.trend.futureOutlook && (
              <Typography variant="body2" color="textSecondary" style={{ marginTop: 8 }}>
                将来見通し: {categoryAnalysis.trend.futureOutlook}
              </Typography>
            )}
          </Box>
        )}

        {/* Lifecycle Information */}
        {categoryAnalysis.lifecycle && (
          <Box mt={2}>
            <Typography variant="subtitle2" gutterBottom>
              技術ライフサイクル
            </Typography>
            <Chip
              label={categoryAnalysis.lifecycle.currentStage}
              color="primary"
              variant="outlined"
              size="small"
            />
            {categoryAnalysis.lifecycle.estimatedTransitionDate && (
              <Typography variant="body2" color="textSecondary" style={{ marginTop: 4 }}>
                次段階予想時期: {new Date(categoryAnalysis.lifecycle.estimatedTransitionDate).toLocaleDateString('ja-JP')}
              </Typography>
            )}
            {categoryAnalysis.lifecycle.investmentRecommendation && (
              <Typography variant="body2" color="textSecondary" style={{ marginTop: 4 }}>
                投資推奨: {categoryAnalysis.lifecycle.investmentRecommendation}
              </Typography>
            )}
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

TechCategoryTrendChart.propTypes = {
  categoryAnalysis: PropTypes.shape({
    categoryName: PropTypes.string,
    categoryCode: PropTypes.string,
    metrics: PropTypes.shape({
      totalBooks: PropTypes.number,
      totalRevenue: PropTypes.number,
      marketShare: PropTypes.number,
      inventoryTurnover: PropTypes.number,
    }),
    trend: PropTypes.shape({
      trendDirection: PropTypes.string,
      growthRate: PropTypes.number,
      trendAnalysis: PropTypes.string,
      futureOutlook: PropTypes.string,
    }),
    subCategories: PropTypes.arrayOf(PropTypes.shape({
      subCategoryName: PropTypes.string,
      revenue: PropTypes.number,
      marketShare: PropTypes.number,
    })),
    lifecycle: PropTypes.shape({
      currentStage: PropTypes.string,
      estimatedTransitionDate: PropTypes.string,
      investmentRecommendation: PropTypes.string,
    }),
  }),
  chartType: PropTypes.oneOf(['composed', 'area']),
  showMetrics: PropTypes.bool,
  height: PropTypes.number,
};

export default TechCategoryTrendChart;