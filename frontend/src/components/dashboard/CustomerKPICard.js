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
import { People, TrendingUp } from '@material-ui/icons';

const useStyles = makeStyles((theme) => ({
  card: {
    height: '100%',
    position: 'relative',
    background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
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

const CustomerKPICard = ({ data, loading }) => {
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
          <People className={classes.icon} />
          {data?.customerGrowth && (
            <Chip
              icon={<TrendingUp />}
              label={`+${data.customerGrowth}%`}
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
          顧客指標
        </Typography>
        
        <Typography className={classes.mainValue}>
          {data?.totalCustomers || 0}
        </Typography>
        
        <Typography className={classes.subValue}>
          総顧客数
        </Typography>
        
        <Typography className={classes.subValue}>
          新規顧客: {data?.newCustomersThisMonth || 0}人/月
        </Typography>
        
        <Typography className={classes.subValue}>
          アクティブ: {data?.activeCustomers || 0}人
        </Typography>
        
        <Typography className={classes.subValue}>
          リテンション: {data?.customerRetentionRate || 0}%
        </Typography>
      </CardContent>
    </Card>
  );
};

export default CustomerKPICard;