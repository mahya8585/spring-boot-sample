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
import { TrendingUp, AttachMoney } from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  card: {
    height: '100%',
    position: 'relative',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    color: 'white',
  },
  cardContent: {
    padding: theme.spacing(2),
  },
  icon: {
    fontSize: 40,
    marginBottom: theme.spacing(1),
    opacity: 0.9,
  },
  mainValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: theme.spacing(1),
  },
  subValue: {
    fontSize: '0.875rem',
    opacity: 0.8,
    marginBottom: theme.spacing(0.5),
  },
  growth: {
    marginTop: theme.spacing(1),
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 120,
  },
}));

const RevenueKPICard = ({ data, loading }) => {
  const classes = useStyles();

  if (loading) {
    return (
      <Card className={classes.card}>
        <CardContent className={classes.loading}>
          <CircularProgress size={40} style={{ color: 'white' }} />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={classes.card}>
      <CardContent className={classes.cardContent}>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <AttachMoney className={classes.icon} />
          {data?.revenueGrowth && (
            <Chip
              icon={<TrendingUp />}
              label={`+${data.revenueGrowth}%`}
              size="small"
              style={{ 
                backgroundColor: 'rgba(255,255,255,0.2)', 
                color: 'white',
                fontWeight: 'bold'
              }}
            />
          )}
        </Box>
        
        <Typography variant="h6" gutterBottom>
          売上指標
        </Typography>
        
        <Typography className={classes.mainValue}>
          ¥{data?.monthRevenue?.toLocaleString() || '0'}
        </Typography>
        
        <Typography className={classes.subValue}>
          今月の売上
        </Typography>
        
        <Typography className={classes.subValue}>
          今日: ¥{data?.todayRevenue?.toLocaleString() || '0'}
        </Typography>
        
        <Typography className={classes.subValue}>
          今週: ¥{data?.weekRevenue?.toLocaleString() || '0'}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default RevenueKPICard;