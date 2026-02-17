# Multi-stage Dockerfile for React 16 frontend
# Stage 1: Build stage
FROM node:16-alpine AS builder

WORKDIR /app

# Copy package files first for dependency caching
COPY package*.json ./

# Install dependencies
RUN npm install --legacy-peer-deps

# Copy source code
COPY public ./public
COPY src ./src

ENV SKIP_PREFLIGHT_CHECK=true

# Build the application
RUN npx react-scripts build

# Stage 2: Production stage with Nginx
FROM nginx:1.25-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Copy template nginx configuration (envsubst at startup)
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Copy built files from builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Create non-root user for Nginx
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# Switch to non-root user
USER nginx

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
