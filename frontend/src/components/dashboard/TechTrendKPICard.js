import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  Chip
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { Code, TrendingUp, TrendingDown } from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  card: {
    height: '100%',
    position: 'relative',
    background: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    color: '#333',
  },
  cardContent: {
    padding: theme.spacing(2),
  },
  icon: {
    fontSize: 40,
    marginBottom: theme.spacing(1),
    opacity: 0.8,
  },
  mainValue: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    marginBottom: theme.spacing(1),
  },
  subValue: {
    fontSize: '0.875rem',
    opacity: 0.7,
    marginBottom: theme.spacing(0.5),
  },
  trendChip: {
    marginTop: theme.spacing(0.5),
    fontSize: '0.75rem',
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 120,
  },
}));

const TechTrendKPICard = ({ data, loading }) => {
  const classes = useStyles();

  if (loading) {
    return (
      <Card className={classes.card}>
        <CardContent className={classes.loading}>
          <CircularProgress size={40} />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={classes.card}>
      <CardContent className={classes.cardContent}>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Code className={classes.icon} />
          <Chip
            label={`指数: ${data?.innovationIndex || 0}`}
            size="small"
            style={{ 
              backgroundColor: 'rgba(255,255,255,0.7)', 
              fontWeight: 'bold'
            }}
          />
        </Box>
        
        <Typography variant="h6" gutterBottom>
          技術トレンド
        </Typography>
        
        <Typography className={classes.mainValue}>
          {data?.topRisingTech || ''}
        </Typography>
        
        <Typography className={classes.subValue}>
          急上昇技術
        </Typography>
        
        {data?.topRisingGrowth && (
          <Chip
            icon={<TrendingUp />}
            label={`+${data.topRisingGrowth}%`}
            size="small"
            className={classes.trendChip}
            style={{ backgroundColor: '#4caf50', color: 'white' }}
          />
        )}
        
        <Box mt={1}>
          <Typography className={classes.subValue}>
            衰退技術: {data?.topFallingTech || ''}
          </Typography>
          {data?.topFallingDecline && (
            <Chip
              icon={<TrendingDown />}
              label={`${data.topFallingDecline}%`}
              size="small"
              className={classes.trendChip}
              style={{ backgroundColor: '#f44336', color: 'white' }}
            />
          )}
        </Box>
        
        <Typography className={classes.subValue} style={{ marginTop: 8 }}>
          新技術: {data?.emergingTechCount || 0} / 陳腐化: {data?.obsoleteTechCount || 0}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default TechTrendKPICard;