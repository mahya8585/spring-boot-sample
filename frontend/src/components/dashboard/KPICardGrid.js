import React from 'react';
import { Grid } from '@material-ui/core';
import RevenueKPICard from './RevenueKPICard';
import InventoryKPICard from './InventoryKPICard';
import CustomerKPICard from './CustomerKPICard';
import TechTrendKPICard from './TechTrendKPICard';

const KPICardGrid = ({ data, loading }) => {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} sm={6} md={3}>
        <RevenueKPICard data={data?.revenue} loading={loading} />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <InventoryKPICard data={data?.inventory} loading={loading} />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <CustomerKPICard data={data?.customers} loading={loading} />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <TechTrendKPICard data={data?.techTrends} loading={loading} />
      </Grid>
    </Grid>
  );
};

export default KPICardGrid;