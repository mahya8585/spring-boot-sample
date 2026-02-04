import React from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardContent,
  Typography,
  makeStyles,
  Chip,
  Box,
  LinearProgress
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
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
  ReferenceLine
} from 'recharts';
import { 
  TrendingUp, 
  Warning, 
  CheckCircle,
  Info
} from '@material-ui/icons';

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
  chartContainer: {
    height: 350,
  },
  confidenceBox: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: theme.spacing(2),
    padding: theme.spacing(1),
    backgroundColor: theme.palette.grey[100],
    borderRadius: theme.shape.borderRadius,
  },
  algorithmChip: {
    marginLeft: theme.spacing(1),
  },
  predictionCard: {
    margin: theme.spacing(1, 0),
    padding: theme.spacing(1),
    backgroundColor: theme.palette.action.hover,
    borderRadius: theme.shape.borderRadius,
  },
  factorsList: {
    listStyle: 'none',
    padding: 0,
    margin: 0,
  },
  factorItem: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: theme.spacing(0.5),
    '& .MuiSvgIcon-root': {
      marginRight: theme.spacing(1),
      fontSize: '1rem',
    },
  },
}));

const DemandPredictionChart = ({ 
  predictionData,
  showConfidence = true,
  showFactors = true,
  height = 350 
}) => {
  const classes = useStyles();

  if (!predictionData) {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6">需要予測</Typography>
          <Typography color="textSecondary">データがありません</Typography>
        </CardContent>
      </Card>
    );
  }

  // Get confidence level color
  const getConfidenceColor = (level) => {
    switch (level) {
      case 'HIGH': return 'primary';
      case 'MEDIUM': return 'default';
      case 'LOW': return 'secondary';
      default: return 'default';
    }
  };

  // Get confidence level icon
  const getConfidenceIcon = (level) => {
    switch (level) {
      case 'HIGH': return <CheckCircle style={{ color: '#4caf50' }} />;
      case 'MEDIUM': return <Info style={{ color: '#ff9800' }} />;
      case 'LOW': return <Warning style={{ color: '#f44336' }} />;
      default: return <Info />;
    }
  };

  // Prepare chart data combining demand predictions and sales predictions
  const chartData = [];
  
  if (predictionData.demandPredictions) {
    predictionData.demandPredictions.forEach(demand => {
      chartData.push({
        date: demand.forecastDate,
        predictedDemand: demand.predictedDemand,
        currentStock: demand.currentStock,
        bookTitle: demand.bookTitle,
        categoryCode: demand.categoryCode,
        confidenceLevel: demand.confidenceLevel,
        type: 'demand'
      });
    });
  }

  if (predictionData.salesPredictions) {
    predictionData.salesPredictions.forEach(sales => {
      const existingEntry = chartData.find(entry => entry.date === sales.forecastPeriodStart);
      if (existingEntry) {
        existingEntry.predictedRevenue = sales.predictedRevenue;
        existingEntry.predictedOrderCount = sales.predictedOrderCount;
      } else {
        chartData.push({
          date: sales.forecastPeriodStart,
          predictedRevenue: sales.predictedRevenue,
          predictedOrderCount: sales.predictedOrderCount,
          categoryName: sales.categoryName,
          type: 'sales'
        });
      }
    });
  }

  // Sort by date
  chartData.sort((a, b) => new Date(a.date) - new Date(b.date));

  // Custom tooltip
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <Card style={{ padding: 8 }}>
          <Typography variant="subtitle2">
            {new Date(label).toLocaleDateString('ja-JP')}
          </Typography>
          {payload.map((entry, index) => (
            <Typography key={index} variant="body2" style={{ color: entry.color }}>
              {entry.name}: {
                entry.name === '予測売上' ? 
                  new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY', notation: 'compact' }).format(entry.value) :
                  entry.value?.toLocaleString()
              }
            </Typography>
          ))}
          {payload[0]?.payload?.bookTitle && (
            <Typography variant="body2" color="textSecondary">
              書籍: {payload[0].payload.bookTitle}
            </Typography>
          )}
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
            需要予測分析
          </Typography>
          <Box>
            <Chip
              label={predictionData.algorithm}
              size="small"
              color="primary"
              className={classes.algorithmChip}
            />
            <Chip
              label={predictionData.timeHorizon}
              size="small"
              variant="outlined"
              className={classes.algorithmChip}
            />
          </Box>
        </div>

        {/* Confidence Indicator */}
        {showConfidence && predictionData.confidence && (
          <div className={classes.confidenceBox}>
            {getConfidenceIcon(predictionData.confidence.confidenceLevel)}
            <Box ml={1} flex={1}>
              <Typography variant="subtitle2">
                予測信頼度: {predictionData.confidence.confidenceLevel}
              </Typography>
              <LinearProgress 
                variant="determinate" 
                value={predictionData.confidence.overallConfidence || 0} 
                style={{ marginTop: 4 }}
              />
              <Typography variant="body2" color="textSecondary">
                {predictionData.confidence.overallConfidence?.toFixed(1)}% 
                (データ品質: {predictionData.confidence.dataQuality})
              </Typography>
            </Box>
          </div>
        )}

        {/* Accuracy Alert */}
        {predictionData.accuracy && (
          <Alert 
            severity={predictionData.accuracy >= 80 ? "success" : predictionData.accuracy >= 60 ? "warning" : "error"}
            style={{ marginBottom: 16 }}
          >
            予測精度: {predictionData.accuracy.toFixed(1)}%
            {predictionData.accuracy < 60 && " - 予測結果は参考程度にご利用ください"}
          </Alert>
        )}

        {/* Chart */}
        <div className={classes.chartContainer}>
          <ResponsiveContainer width="100%" height={height}>
            <ComposedChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="date" 
                tickFormatter={(date) => new Date(date).toLocaleDateString('ja-JP', { month: 'short', day: 'numeric' })}
              />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip content={<CustomTooltip />} />
              <Legend />
              
              {/* Current stock line */}
              <Line
                yAxisId="left"
                type="monotone"
                dataKey="currentStock"
                stroke="#82ca9d"
                strokeWidth={2}
                strokeDasharray="5 5"
                name="現在在庫"
                connectNulls={false}
              />
              
              {/* Predicted demand bars */}
              <Bar 
                yAxisId="left" 
                dataKey="predictedDemand" 
                fill="#8884d8" 
                name="予測需要"
                opacity={0.8}
              />
              
              {/* Predicted revenue line */}
              <Line
                yAxisId="right"
                type="monotone"
                dataKey="predictedRevenue"
                stroke="#ff7300"
                strokeWidth={2}
                name="予測売上"
                connectNulls={false}
              />
              
              {/* Reorder point reference */}
              <ReferenceLine 
                yAxisId="left" 
                y={10} 
                stroke="red" 
                strokeDasharray="3 3"
                label="発注点"
              />
            </ComposedChart>
          </ResponsiveContainer>
        </div>

        {/* Key Predictions */}
        {predictionData.demandPredictions && predictionData.demandPredictions.length > 0 && (
          <Box mt={2}>
            <Typography variant="subtitle2" gutterBottom>
              主要予測項目
            </Typography>
            {predictionData.demandPredictions.slice(0, 3).map((prediction, index) => (
              <div key={index} className={classes.predictionCard}>
                <Typography variant="body2" fontWeight="bold">
                  {prediction.bookTitle}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  予測需要: {prediction.predictedDemand}冊 
                  (現在在庫: {prediction.currentStock}冊)
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  トレンド: {prediction.demandTrend} 
                  {prediction.confidenceLevel && (
                    <Chip 
                      label={`${prediction.confidenceLevel.toFixed(0)}%信頼度`}
                      size="small"
                      color={getConfidenceColor(
                        prediction.confidenceLevel >= 80 ? 'HIGH' : 
                        prediction.confidenceLevel >= 60 ? 'MEDIUM' : 'LOW'
                      )}
                      style={{ marginLeft: 8 }}
                    />
                  )}
                </Typography>
              </div>
            ))}
          </Box>
        )}

        {/* Seasonal Factors */}
        {showFactors && predictionData.seasonalFactors && predictionData.seasonalFactors.length > 0 && (
          <Box mt={2}>
            <Typography variant="subtitle2" gutterBottom>
              季節性要因
            </Typography>
            <ul className={classes.factorsList}>
              {predictionData.seasonalFactors.map((factor, index) => (
                <li key={index} className={classes.factorItem}>
                  <TrendingUp />
                  <Typography variant="body2">
                    {factor.season}: {factor.seasonalMultiplier?.toFixed(2)}倍
                    {factor.description && ` (${factor.description})`}
                  </Typography>
                </li>
              ))}
            </ul>
          </Box>
        )}

        {/* Uncertainty Factors */}
        {predictionData.confidence?.uncertaintyFactors && predictionData.confidence.uncertaintyFactors.length > 0 && (
          <Box mt={2}>
            <Typography variant="subtitle2" gutterBottom>
              不確実性要因
            </Typography>
            <ul className={classes.factorsList}>
              {predictionData.confidence.uncertaintyFactors.map((factor, index) => (
                <li key={index} className={classes.factorItem}>
                  <Warning />
                  <Typography variant="body2" color="textSecondary">
                    {factor}
                  </Typography>
                </li>
              ))}
            </ul>
          </Box>
        )}

        {/* Recommendation */}
        {predictionData.confidence?.recommendation && (
          <Alert severity="info" style={{ marginTop: 16 }}>
            {predictionData.confidence.recommendation}
          </Alert>
        )}
      </CardContent>
    </Card>
  );
};

DemandPredictionChart.propTypes = {
  predictionData: PropTypes.shape({
    algorithm: PropTypes.string,
    timeHorizon: PropTypes.string,
    accuracy: PropTypes.number,
    demandPredictions: PropTypes.arrayOf(PropTypes.shape({
      bookTitle: PropTypes.string,
      forecastDate: PropTypes.string,
      predictedDemand: PropTypes.number,
      currentStock: PropTypes.number,
      demandTrend: PropTypes.string,
      confidenceLevel: PropTypes.number,
    })),
    salesPredictions: PropTypes.arrayOf(PropTypes.shape({
      categoryName: PropTypes.string,
      forecastPeriodStart: PropTypes.string,
      predictedRevenue: PropTypes.number,
      predictedOrderCount: PropTypes.number,
    })),
    seasonalFactors: PropTypes.arrayOf(PropTypes.shape({
      season: PropTypes.string,
      seasonalMultiplier: PropTypes.number,
      description: PropTypes.string,
    })),
    confidence: PropTypes.shape({
      overallConfidence: PropTypes.number,
      confidenceLevel: PropTypes.string,
      dataQuality: PropTypes.string,
      uncertaintyFactors: PropTypes.arrayOf(PropTypes.string),
      recommendation: PropTypes.string,
    }),
  }),
  showConfidence: PropTypes.bool,
  showFactors: PropTypes.bool,
  height: PropTypes.number,
};

export default DemandPredictionChart;