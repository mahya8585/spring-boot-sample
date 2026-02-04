import React, { useState } from 'react';
import {
  Paper,
  Typography,
  Box,
  CircularProgress,
  Grid,
  Tabs,
  Tab,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Chip,
} from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { TrendingUp, TrendingDown, Assessment } from '@material-ui/icons';
import {
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area,
  ComposedChart,
  ScatterChart,
  Scatter
} from 'recharts';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    height: 400,
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 300,
  },
  chartTitle: {
    marginBottom: theme.spacing(2),
    fontWeight: 'bold',
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(1),
  },
  controls: {
    marginBottom: theme.spacing(2),
    display: 'flex',
    gap: theme.spacing(2),
    alignItems: 'center',
  },
  trendChip: {
    margin: theme.spacing(0.5),
  },
  interactiveChart: {
    cursor: 'pointer',
    '&:hover': {
      opacity: 0.8,
    },
  },
}));

// Enhanced sample data for different analysis types
const sampleTrendData = [
  { name: '1月', revenue: 65000, orders: 120, customers: 45, ai_ml: 18000, web_dev: 15000, cloud: 12000 },
  { name: '2月', revenue: 72000, orders: 135, customers: 52, ai_ml: 21000, web_dev: 16500, cloud: 13500 },
  { name: '3月', revenue: 68000, orders: 128, customers: 48, ai_ml: 19500, web_dev: 15800, cloud: 12800 },
  { name: '4月', revenue: 78000, orders: 145, customers: 58, ai_ml: 23500, web_dev: 17200, cloud: 15000 },
  { name: '5月', revenue: 85000, orders: 162, customers: 63, ai_ml: 27000, web_dev: 18500, cloud: 16200 },
  { name: '6月', revenue: 92000, orders: 178, customers: 71, ai_ml: 31000, web_dev: 19800, cloud: 17500 },
];

const techTrendData = [
  { tech: 'AI/ML', growth: 24.8, status: 'RISING', impact: 125000, risk: 'LOW' },
  { tech: 'Cloud', growth: 18.5, status: 'RISING', impact: 98000, risk: 'LOW' },
  { tech: 'Web Dev', growth: 8.3, status: 'STABLE', impact: 87000, risk: 'MEDIUM' },
  { tech: 'Mobile', growth: 12.1, status: 'RISING', impact: 76000, risk: 'LOW' },
  { tech: 'DevOps', growth: 15.7, status: 'RISING', impact: 65000, risk: 'LOW' },
  { tech: 'jQuery', growth: -15.3, status: 'FALLING', impact: 12000, risk: 'HIGH' },
];

const seasonalData = [
  { month: 'Jan', academic: 85, professional: 65, hobbyist: 45 },
  { month: 'Feb', academic: 92, professional: 68, hobbyist: 48 },
  { month: 'Mar', academic: 78, professional: 72, hobbyist: 52 },
  { month: 'Apr', academic: 125, professional: 85, hobbyist: 58 },
  { month: 'May', academic: 98, professional: 78, hobbyist: 48 },
  { month: 'Jun', academic: 65, professional: 82, hobbyist: 55 },
];

const customerJourneyData = [
  { stage: 'Newcomer', count: 45, avgValue: 2800, techCategories: 1.2 },
  { stage: 'Developing', count: 78, avgValue: 5200, techCategories: 2.8 },
  { stage: 'Advanced', count: 34, avgValue: 8900, techCategories: 4.5 },
  { stage: 'Expert', count: 12, avgValue: 15600, techCategories: 6.8 },
];

const TrendChartGrid = ({ data, loading, onDrillDown }) => {
  const classes = useStyles();
  const [selectedTab, setSelectedTab] = useState(0);
  const [timeRange, setTimeRange] = useState('6months');
  const [selectedTechCategory, setSelectedTechCategory] = useState('all');

  if (loading) {
    return (
      <Paper className={classes.paper}>
        <div className={classes.loading}>
          <CircularProgress size={60} />
        </div>
      </Paper>
    );
  }

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  const handleDrillDown = (chartType, dataPoint) => {
    if (onDrillDown) {
      onDrillDown(chartType, dataPoint);
    }
  };

  const renderSalesAnalysisCharts = () => (
    <Grid container spacing={2}>
      {/* Enhanced Revenue Trend with Tech Categories */}
      <Grid item xs={12}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            <TrendingUp />
            技術カテゴリ別売上トレンド分析
          </Typography>
          <div className={classes.controls}>
            <FormControl variant="outlined" size="small">
              <InputLabel>期間</InputLabel>
              <Select
                value={timeRange}
                onChange={(e) => setTimeRange(e.target.value)}
                label="期間"
              >
                <MenuItem value="3months">3ヶ月</MenuItem>
                <MenuItem value="6months">6ヶ月</MenuItem>
                <MenuItem value="1year">1年</MenuItem>
              </Select>
            </FormControl>
            <FormControl variant="outlined" size="small">
              <InputLabel>技術カテゴリ</InputLabel>
              <Select
                value={selectedTechCategory}
                onChange={(e) => setSelectedTechCategory(e.target.value)}
                label="技術カテゴリ"
              >
                <MenuItem value="all">全て</MenuItem>
                <MenuItem value="ai_ml">AI/ML</MenuItem>
                <MenuItem value="web_dev">Web開発</MenuItem>
                <MenuItem value="cloud">クラウド</MenuItem>
              </Select>
            </FormControl>
            <Button 
              variant="outlined" 
              size="small"
              onClick={() => handleDrillDown('sales_trend', { timeRange, category: selectedTechCategory })}
            >
              詳細分析
            </Button>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <ComposedChart data={sampleTrendData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip 
                formatter={(value, name) => [
                  typeof value === 'number' && value > 1000 ? `¥${value.toLocaleString()}` : value,
                  name
                ]}
              />
              <Bar yAxisId="left" dataKey="ai_ml" fill="#8884d8" name="AI/ML" />
              <Bar yAxisId="left" dataKey="web_dev" fill="#82ca9d" name="Web開発" />
              <Bar yAxisId="left" dataKey="cloud" fill="#ffc658" name="クラウド" />
              <Line yAxisId="right" type="monotone" dataKey="revenue" stroke="#ff7300" strokeWidth={3} name="総売上" />
            </ComposedChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>

      {/* Tech Trend Risk Analysis */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            <Assessment />
            技術トレンド・リスク分析
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <ScatterChart data={techTrendData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="growth" name="成長率" unit="%" />
              <YAxis dataKey="impact" name="影響金額" unit="¥" />
              <Tooltip 
                cursor={{ strokeDasharray: '3 3' }}
                content={({ active, payload, label }) => {
                  if (active && payload && payload.length) {
                    const data = payload[0].payload;
                    return (
                      <div style={{ backgroundColor: 'white', padding: '10px', border: '1px solid #ccc' }}>
                        <p><strong>{data.tech}</strong></p>
                        <p>成長率: {data.growth}%</p>
                        <p>影響金額: ¥{data.impact.toLocaleString()}</p>
                        <p>リスク: {data.risk}</p>
                        <p>ステータス: {data.status}</p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Scatter 
                name="技術トレンド" 
                dataKey="impact" 
                fill="#8884d8"
                onClick={(data) => handleDrillDown('tech_trend', data)}
                className={classes.interactiveChart}
              />
            </ScatterChart>
          </ResponsiveContainer>
          <Box mt={1}>
            {techTrendData.map((tech, index) => (
              <Chip
                key={index}
                label={`${tech.tech} (${tech.growth > 0 ? '+' : ''}${tech.growth}%)`}
                color={tech.growth > 0 ? 'primary' : tech.growth < -10 ? 'secondary' : 'default'}
                icon={tech.growth > 0 ? <TrendingUp /> : <TrendingDown />}
                className={classes.trendChip}
                size="small"
              />
            ))}
          </Box>
        </Paper>
      </Grid>

      {/* Seasonal Analysis */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            季節性・顧客セグメント分析
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={seasonalData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Area 
                type="monotone" 
                dataKey="academic" 
                stackId="1" 
                stroke="#8884d8" 
                fill="#8884d8" 
                name="学術関係者"
              />
              <Area 
                type="monotone" 
                dataKey="professional" 
                stackId="1" 
                stroke="#82ca9d" 
                fill="#82ca9d" 
                name="プロフェッショナル"
              />
              <Area 
                type="monotone" 
                dataKey="hobbyist" 
                stackId="1" 
                stroke="#ffc658" 
                fill="#ffc658" 
                name="趣味・学習者"
              />
            </AreaChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
    </Grid>
  );

  const renderCustomerAnalysisCharts = () => (
    <Grid container spacing={2}>
      {/* Customer Journey Analysis */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            顧客学習ジャーニー分析
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <ComposedChart data={customerJourneyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="stage" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip />
              <Bar yAxisId="left" dataKey="count" fill="#8884d8" name="顧客数" />
              <Line yAxisId="right" type="monotone" dataKey="avgValue" stroke="#ff7300" strokeWidth={3} name="平均購入額" />
              <Line yAxisId="right" type="monotone" dataKey="techCategories" stroke="#82ca9d" strokeWidth={2} name="技術カテゴリ数" />
            </ComposedChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>

      {/* Tech Skill Progression */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            技術スキル進展パターン
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={[
              { path: 'Frontend', frequency: 45, success: 87.5 },
              { path: 'Backend', frequency: 38, success: 82.3 },
              { path: 'Full-stack', frequency: 32, success: 79.1 },
              { path: 'Data Science', frequency: 28, success: 85.7 },
              { path: 'DevOps', frequency: 22, success: 78.9 },
            ]}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="path" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip />
              <Bar yAxisId="left" dataKey="frequency" fill="#8884d8" name="頻度" />
              <Bar yAxisId="right" dataKey="success" fill="#82ca9d" name="成功率%" />
            </BarChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
    </Grid>
  );

  const renderInventoryAnalysisCharts = () => (
    <Grid container spacing={2}>
      {/* Inventory Turnover by Category */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            カテゴリ別在庫回転率
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={[
              { category: 'AI/ML', turnover: 6.2, stock: 85, deadStock: 2 },
              { category: 'Web Dev', turnover: 4.8, stock: 120, deadStock: 8 },
              { category: 'Cloud', turnover: 5.1, stock: 95, deadStock: 3 },
              { category: 'Mobile', turnover: 3.9, stock: 78, deadStock: 12 },
              { category: 'Database', turnover: 3.2, stock: 65, deadStock: 15 },
            ]}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="category" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip />
              <Bar yAxisId="left" dataKey="turnover" fill="#8884d8" name="回転率" />
              <Bar yAxisId="right" dataKey="deadStock" fill="#ff7300" name="デッドストック数" />
            </BarChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>

      {/* Tech Obsolescence Risk */}
      <Grid item xs={12} md={6}>
        <Paper className={classes.paper}>
          <Typography variant="h6" className={classes.chartTitle}>
            技術陳腐化リスク評価
          </Typography>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={[
                  { name: '低リスク', value: 65, color: '#82ca9d' },
                  { name: '中リスク', value: 25, color: '#ffc658' },
                  { name: '高リスク', value: 10, color: '#ff7300' },
                ]}
                cx="50%"
                cy="50%"
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
                label={({ name, value }) => `${name}: ${value}%`}
              >
                {[{ color: '#82ca9d' }, { color: '#ffc658' }, { color: '#ff7300' }].map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => [`${value}%`, '割合']} />
            </PieChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
    </Grid>
  );

  return (
    <Box>
      <Paper style={{ marginBottom: 16 }}>
        <Tabs
          value={selectedTab}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
        >
          <Tab label="売上分析" />
          <Tab label="顧客分析" />
          <Tab label="在庫分析" />
        </Tabs>
      </Paper>

      {selectedTab === 0 && renderSalesAnalysisCharts()}
      {selectedTab === 1 && renderCustomerAnalysisCharts()}
      {selectedTab === 2 && renderInventoryAnalysisCharts()}
    </Box>
  );
};

export default TrendChartGrid;