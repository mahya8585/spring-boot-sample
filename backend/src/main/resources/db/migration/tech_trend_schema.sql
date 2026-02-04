-- Tech Trend Analysis Feature - Database Schema Migration
-- 技術トレンド分析機能のデータベーススキーマ移行

-- 1. Tech Trend Analysis Table
-- 技術トレンド分析テーブル
CREATE TABLE tech_trend_analysis (
    id BIGSERIAL PRIMARY KEY,
    tech_category_id BIGINT NOT NULL REFERENCES tech_categories(id),
    analysis_date DATE NOT NULL,
    total_revenue DECIMAL(12,2),
    total_units_sold INTEGER,
    growth_rate DECIMAL(5,2),
    market_share DECIMAL(5,2),
    lifecycle_stage VARCHAR(20) CHECK (lifecycle_stage IN ('EMERGING', 'GROWTH', 'MATURITY', 'DECLINE')),
    trend_direction VARCHAR(20) CHECK (trend_direction IN ('RISING', 'STABLE', 'DECLINING')),
    emerging_score DECIMAL(5,2),
    obsolescence_risk DECIMAL(5,2),
    trend_analysis TEXT,
    investment_recommendation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tech Relationships Table
-- 技術関連性分析テーブル
CREATE TABLE tech_relationships (
    id BIGSERIAL PRIMARY KEY,
    primary_tech_id BIGINT NOT NULL REFERENCES tech_categories(id),
    related_tech_id BIGINT NOT NULL REFERENCES tech_categories(id),
    relationship_type VARCHAR(20) NOT NULL CHECK (relationship_type IN ('COMPLEMENTARY', 'COMPETITIVE', 'PREREQUISITE', 'SUCCESSOR')),
    correlation_strength DECIMAL(5,2) NOT NULL CHECK (correlation_strength BETWEEN -1.0 AND 1.0),
    analysis_date DATE NOT NULL,
    confidence_level VARCHAR(10) CHECK (confidence_level IN ('HIGH', 'MEDIUM', 'LOW')),
    statistical_significance DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(primary_tech_id, related_tech_id, analysis_date)
);

-- 3. Tech Predictions Table
-- 技術予測データテーブル
CREATE TABLE tech_predictions (
    id BIGSERIAL PRIMARY KEY,
    tech_category_id BIGINT NOT NULL REFERENCES tech_categories(id),
    prediction_date DATE NOT NULL,
    prediction_for_date DATE NOT NULL,
    predicted_revenue DECIMAL(12,2),
    predicted_growth_rate DECIMAL(5,2),
    confidence_interval DECIMAL(5,2),
    prediction_model VARCHAR(50),
    model_accuracy DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Performance Optimization
-- パフォーマンス最適化用インデックス

-- Tech Trend Analysis Indexes
CREATE INDEX idx_tech_trend_analysis_date_category ON tech_trend_analysis(analysis_date, tech_category_id);
CREATE INDEX idx_tech_trend_analysis_lifecycle ON tech_trend_analysis(lifecycle_stage);
CREATE INDEX idx_tech_trend_analysis_emerging ON tech_trend_analysis(emerging_score DESC);
CREATE INDEX idx_tech_trend_analysis_growth ON tech_trend_analysis(growth_rate DESC);

-- Tech Relationships Indexes
CREATE INDEX idx_tech_relationships_primary_type ON tech_relationships(primary_tech_id, relationship_type);
CREATE INDEX idx_tech_relationships_correlation ON tech_relationships(correlation_strength DESC);
CREATE INDEX idx_tech_relationships_date ON tech_relationships(analysis_date DESC);

-- Tech Predictions Indexes
CREATE INDEX idx_tech_predictions_category_date ON tech_predictions(tech_category_id, prediction_for_date);
CREATE INDEX idx_tech_predictions_model ON tech_predictions(prediction_model);
CREATE INDEX idx_tech_predictions_accuracy ON tech_predictions(model_accuracy DESC);

-- Sample Data for Tech Trend Analysis
-- 技術トレンド分析のサンプルデータ

INSERT INTO tech_trend_analysis (tech_category_id, analysis_date, total_revenue, total_units_sold, growth_rate, market_share, lifecycle_stage, trend_direction, emerging_score, obsolescence_risk, trend_analysis, investment_recommendation)
SELECT 
    tc.id,
    CURRENT_DATE,
    CASE tc.category_code
        WHEN 'JAVA' THEN 15000.00
        WHEN 'PYTHON' THEN 12000.00
        WHEN 'JAVASCRIPT' THEN 10000.00
        WHEN 'REACT' THEN 8000.00
        WHEN 'SPRING' THEN 6000.00
        ELSE 3000.00
    END as total_revenue,
    CASE tc.category_code
        WHEN 'JAVA' THEN 45
        WHEN 'PYTHON' THEN 38
        WHEN 'JAVASCRIPT' THEN 35
        WHEN 'REACT' THEN 28
        WHEN 'SPRING' THEN 22
        ELSE 15
    END as total_units_sold,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 15.5
        WHEN 'REACT' THEN 12.3
        WHEN 'JAVASCRIPT' THEN 8.7
        WHEN 'JAVA' THEN 5.2
        WHEN 'SPRING' THEN 3.1
        ELSE 0.0
    END as growth_rate,
    CASE tc.category_code
        WHEN 'JAVA' THEN 33.3
        WHEN 'PYTHON' THEN 26.7
        WHEN 'JAVASCRIPT' THEN 22.2
        WHEN 'REACT' THEN 17.8
        ELSE 10.0
    END as market_share,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'GROWTH'
        WHEN 'REACT' THEN 'EMERGING'
        WHEN 'JAVA' THEN 'MATURITY'
        WHEN 'JAVASCRIPT' THEN 'MATURITY'
        ELSE 'STABLE'
    END as lifecycle_stage,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'RISING'
        WHEN 'REACT' THEN 'RISING'
        WHEN 'JAVA' THEN 'STABLE'
        ELSE 'STABLE'
    END as trend_direction,
    CASE tc.category_code
        WHEN 'REACT' THEN 85.0
        WHEN 'PYTHON' THEN 78.5
        WHEN 'JAVASCRIPT' THEN 65.0
        ELSE 45.0
    END as emerging_score,
    CASE tc.category_code
        WHEN 'JAVA' THEN 25.0
        WHEN 'JAVASCRIPT' THEN 30.0
        ELSE 15.0
    END as obsolescence_risk,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'Strong growth driven by AI/ML adoption and data science trends'
        WHEN 'REACT' THEN 'Rapid adoption in modern web development, high developer interest'
        WHEN 'JAVA' THEN 'Mature technology with stable enterprise adoption'
        ELSE 'Standard technology lifecycle analysis'
    END as trend_analysis,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'EXPAND - Increase inventory for AI/ML and data science books'
        WHEN 'REACT' THEN 'INVEST - Strong growth potential in frontend development'
        WHEN 'JAVA' THEN 'MAINTAIN - Steady enterprise demand'
        ELSE 'MONITOR - Watch for trend changes'
    END as investment_recommendation
FROM tech_categories tc
WHERE tc.category_code IN ('JAVA', 'PYTHON', 'JAVASCRIPT', 'REACT', 'SPRING', 'MYSQL');

-- Sample Tech Relationships
-- サンプル技術関連性データ

INSERT INTO tech_relationships (primary_tech_id, related_tech_id, relationship_type, correlation_strength, analysis_date, confidence_level, statistical_significance)
SELECT 
    tc1.id,
    tc2.id,
    'COMPLEMENTARY',
    0.75,
    CURRENT_DATE,
    'HIGH',
    0.95
FROM tech_categories tc1, tech_categories tc2
WHERE tc1.category_code = 'REACT' AND tc2.category_code = 'JAVASCRIPT'

UNION ALL

SELECT 
    tc1.id,
    tc2.id,
    'COMPLEMENTARY',
    0.82,
    CURRENT_DATE,
    'HIGH',
    0.97
FROM tech_categories tc1, tech_categories tc2
WHERE tc1.category_code = 'SPRING' AND tc2.category_code = 'JAVA'

UNION ALL

SELECT 
    tc1.id,
    tc2.id,
    'COMPETITIVE',
    -0.45,
    CURRENT_DATE,
    'MEDIUM',
    0.75
FROM tech_categories tc1, tech_categories tc2
WHERE tc1.category_code = 'PYTHON' AND tc2.category_code = 'JAVA';

-- Sample Tech Predictions
-- サンプル技術予測データ

INSERT INTO tech_predictions (tech_category_id, prediction_date, prediction_for_date, predicted_revenue, predicted_growth_rate, confidence_interval, prediction_model, model_accuracy)
SELECT 
    tc.id,
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '3 months',
    CASE tc.category_code
        WHEN 'PYTHON' THEN 14000.00
        WHEN 'REACT' THEN 9500.00
        WHEN 'JAVA' THEN 15200.00
        ELSE 3500.00
    END as predicted_revenue,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 18.2
        WHEN 'REACT' THEN 15.7
        WHEN 'JAVA' THEN 6.1
        ELSE 2.5
    END as predicted_growth_rate,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 8.5
        WHEN 'REACT' THEN 12.3
        WHEN 'JAVA' THEN 5.2
        ELSE 15.0
    END as confidence_interval,
    'LINEAR_REGRESSION',
    CASE tc.category_code
        WHEN 'PYTHON' THEN 82.5
        WHEN 'REACT' THEN 75.8
        WHEN 'JAVA' THEN 88.2
        ELSE 70.0
    END as model_accuracy
FROM tech_categories tc
WHERE tc.category_code IN ('PYTHON', 'REACT', 'JAVA', 'JAVASCRIPT', 'SPRING');