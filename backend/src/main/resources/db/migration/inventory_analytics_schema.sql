-- Inventory Analytics Schema Enhancement
-- Phase 1: Advanced inventory analytics tables for comprehensive reporting

-- Demand forecasts table
CREATE TABLE IF NOT EXISTS demand_forecasts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    forecast_date DATE NOT NULL,
    predicted_demand INTEGER NOT NULL,
    algorithm VARCHAR(50) NOT NULL DEFAULT 'SEASONAL',
    confidence DECIMAL(5,4) NOT NULL DEFAULT 0.7500,
    parameters TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- ABC/XYZ analysis results table
CREATE TABLE IF NOT EXISTS abc_xyz_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    abc_category VARCHAR(1) NOT NULL CHECK (abc_category IN ('A', 'B', 'C')),
    xyz_category VARCHAR(1) NOT NULL CHECK (xyz_category IN ('X', 'Y', 'Z')),
    sales_contribution DECIMAL(5,2) NOT NULL,
    demand_variability DECIMAL(5,2) NOT NULL,
    recommended_strategy VARCHAR(100),
    analysis_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE KEY unique_book_analysis (book_id, analysis_date)
);

-- Obsolescence risk assessments table
CREATE TABLE IF NOT EXISTS obsolescence_assessments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    risk_level VARCHAR(10) NOT NULL CHECK (risk_level IN ('HIGH', 'MEDIUM', 'LOW')),
    months_to_obsolescence INTEGER,
    risk_score DECIMAL(5,2) NOT NULL,
    mitigation_strategy VARCHAR(200),
    assessment_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Seasonal patterns table
CREATE TABLE IF NOT EXISTS seasonal_patterns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    category_code VARCHAR(50),
    season VARCHAR(20) NOT NULL,
    seasonal_index DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    year_data INTEGER NOT NULL,
    average_demand DECIMAL(10,2),
    peak_month INTEGER,
    low_month INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Optimal stock settings table
CREATE TABLE IF NOT EXISTS optimal_stock_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    optimal_stock_level INTEGER NOT NULL,
    safety_stock_level INTEGER NOT NULL DEFAULT 0,
    reorder_point INTEGER NOT NULL,
    reorder_quantity INTEGER NOT NULL,
    service_level DECIMAL(5,2) NOT NULL DEFAULT 95.00,
    lead_time_days INTEGER NOT NULL DEFAULT 7,
    calculation_method VARCHAR(50) NOT NULL DEFAULT 'STATISTICAL',
    last_calculated DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE KEY unique_book_optimal (book_id)
);

-- Create indexes for performance
CREATE INDEX idx_demand_forecasts_book_date ON demand_forecasts(book_id, forecast_date);
CREATE INDEX idx_abc_xyz_analysis_date ON abc_xyz_analysis(analysis_date);
CREATE INDEX idx_obsolescence_risk_level ON obsolescence_assessments(risk_level, assessment_date);
CREATE INDEX idx_seasonal_patterns_season ON seasonal_patterns(season, year_data);
CREATE INDEX idx_optimal_stock_book ON optimal_stock_settings(book_id);