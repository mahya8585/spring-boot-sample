import React from 'react';
import PropTypes from 'prop-types';
import {
  Card,
  CardContent,
  Typography,
  makeStyles,
  Grid,
  Paper,
  Tooltip
} from '@material-ui/core';
import {
  ResponsiveContainer,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ScatterChart,
  Scatter
} from 'recharts';

const useStyles = makeStyles((theme) => ({
  root: {
    height: '100%',
  },
  heatmapGrid: {
    padding: theme.spacing(2),
  },
  heatmapCell: {
    minHeight: 60,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    margin: theme.spacing(0.5),
    borderRadius: theme.shape.borderRadius,
    cursor: 'pointer',
    transition: 'all 0.3s ease',
    '&:hover': {
      transform: 'scale(1.05)',
      boxShadow: theme.shadows[4],
    },
  },
  cellText: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  legend: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing(2),
  },
  legendItem: {
    display: 'flex',
    alignItems: 'center',
    margin: theme.spacing(0, 1),
  },
  legendColor: {
    width: 16,
    height: 16,
    marginRight: theme.spacing(0.5),
    borderRadius: 2,
  },
}));

const ProfitabilityHeatmap = ({ profitabilityData, displayMode = 'grid' }) => {
  const classes = useStyles();

  if (!profitabilityData || profitabilityData.length === 0) {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6">収益性ヒートマップ</Typography>
          <Typography color="textSecondary">データがありません</Typography>
        </CardContent>
      </Card>
    );
  }

  // Calculate profitability ranges for color coding
  const profits = profitabilityData.map(item => parseFloat(item.profitMargin || 0));
  const minProfit = Math.min(...profits);
  const maxProfit = Math.max(...profits);

  // Get color based on profitability
  const getProfitabilityColor = (profitMargin) => {
    const normalizedValue = (profitMargin - minProfit) / (maxProfit - minProfit);
    
    if (normalizedValue >= 0.8) return '#4caf50'; // Excellent
    if (normalizedValue >= 0.6) return '#8bc34a'; // Good
    if (normalizedValue >= 0.4) return '#ffeb3b'; // Average
    if (normalizedValue >= 0.2) return '#ff9800'; // Poor
    return '#f44336'; // Very poor
  };

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
      notation: 'compact'
    }).format(value || 0);
  };

  // Custom tooltip for scatter plot
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <Paper style={{ padding: 8 }}>
          <Typography variant="subtitle2">{data.itemName}</Typography>
          <Typography variant="body2">売上: {formatCurrency(data.revenue)}</Typography>
          <Typography variant="body2">利益: {formatCurrency(data.profit)}</Typography>
          <Typography variant="body2">利益率: {data.profitMargin?.toFixed(1)}%</Typography>
          <Typography variant="body2">ROI: {data.roi?.toFixed(1)}%</Typography>
        </Paper>
      );
    }
    return null;
  };

  if (displayMode === 'scatter') {
    return (
      <Card className={classes.root}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            収益性スキャッター分析
          </Typography>
          <ResponsiveContainer width="100%" height={400}>
            <ScatterChart data={profitabilityData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="revenue" 
                name="売上"
                tickFormatter={(value) => formatCurrency(value)}
              />
              <YAxis 
                dataKey="profitMargin" 
                name="利益率"
                tickFormatter={(value) => `${value}%`}
              />
              <RechartsTooltip content={<CustomTooltip />} />
              <Scatter 
                dataKey="profitMargin" 
                fill="#8884d8"
              >
                {profitabilityData.map((entry, index) => (
                  <Cell 
                    key={`cell-${index}`} 
                    fill={getProfitabilityColor(entry.profitMargin)}
                  />
                ))}
              </Scatter>
            </ScatterChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={classes.root}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          収益性ヒートマップ
        </Typography>
        
        <Grid container className={classes.heatmapGrid}>
          {profitabilityData.map((item, index) => (
            <Grid item xs={6} sm={4} md={3} key={index}>
              <Tooltip
                title={
                  <div>
                    <Typography variant="body2">{item.itemName}</Typography>
                    <Typography variant="body2">売上: {formatCurrency(item.revenue)}</Typography>
                    <Typography variant="body2">利益: {formatCurrency(item.profit)}</Typography>
                    <Typography variant="body2">利益率: {item.profitMargin?.toFixed(1)}%</Typography>
                    {item.roi && (
                      <Typography variant="body2">ROI: {item.roi.toFixed(1)}%</Typography>
                    )}
                  </div>
                }
              >
                <Paper 
                  className={classes.heatmapCell}
                  style={{ 
                    backgroundColor: getProfitabilityColor(item.profitMargin),
                    minHeight: 80
                  }}
                >
                  <div className={classes.cellText}>
                    <Typography variant="body2" style={{ fontSize: '0.75rem' }}>
                      {item.itemName?.length > 12 ? 
                        `${item.itemName.substring(0, 12)}...` : 
                        item.itemName}
                    </Typography>
                    <Typography variant="h6">
                      {item.profitMargin?.toFixed(1)}%
                    </Typography>
                  </div>
                </Paper>
              </Tooltip>
            </Grid>
          ))}
        </Grid>

        {/* Legend */}
        <div className={classes.legend}>
          <div className={classes.legendItem}>
            <div className={classes.legendColor} style={{ backgroundColor: '#f44336' }} />
            <Typography variant="body2">低収益</Typography>
          </div>
          <div className={classes.legendItem}>
            <div className={classes.legendColor} style={{ backgroundColor: '#ff9800' }} />
            <Typography variant="body2">やや低</Typography>
          </div>
          <div className={classes.legendItem}>
            <div className={classes.legendColor} style={{ backgroundColor: '#ffeb3b' }} />
            <Typography variant="body2">平均</Typography>
          </div>
          <div className={classes.legendItem}>
            <div className={classes.legendColor} style={{ backgroundColor: '#8bc34a' }} />
            <Typography variant="body2">良好</Typography>
          </div>
          <div className={classes.legendItem}>
            <div className={classes.legendColor} style={{ backgroundColor: '#4caf50' }} />
            <Typography variant="body2">優秀</Typography>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

ProfitabilityHeatmap.propTypes = {
  profitabilityData: PropTypes.arrayOf(PropTypes.shape({
    itemName: PropTypes.string.isRequired,
    revenue: PropTypes.number,
    profit: PropTypes.number,
    profitMargin: PropTypes.number,
    roi: PropTypes.number,
  })),
  displayMode: PropTypes.oneOf(['grid', 'scatter']),
};

export default ProfitabilityHeatmap;