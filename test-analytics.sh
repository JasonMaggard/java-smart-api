#!/bin/bash

# Smart API - Analytics Testing Script
# This script demonstrates Phase 4: Usage Analytics functionality

BASE_URL="http://localhost:8080"

echo "🔍 Smart API - Usage Analytics Demo"
echo "===================================="
echo ""

# 1. Overall Statistics
echo "📊 1. Overall Usage Statistics"
echo "   GET /api/usage/stats"
curl -s "$BASE_URL/api/usage/stats" | python3 -m json.tool
echo ""
echo ""

# 2. Top Endpoints
echo "🏆 2. Top 5 Most Used Endpoints"
echo "   GET /api/usage/top-endpoints?limit=5"
curl -s "$BASE_URL/api/usage/top-endpoints?limit=5" | python3 -m json.tool
echo ""
echo ""

# 3. Slow Endpoints
echo "🐌 3. Top 5 Slowest Endpoints"
echo "   GET /api/usage/slow-endpoints?limit=5"
curl -s "$BASE_URL/api/usage/slow-endpoints?limit=5" | python3 -m json.tool
echo ""
echo ""

# 4. Specific Endpoint Stats
echo "🎯 4. Specific Endpoint Statistics"
echo "   GET /api/usage/by-endpoint?path=/api/usage/stats&method=GET"
curl -s "$BASE_URL/api/usage/by-endpoint?path=/api/usage/stats&method=GET" | python3 -m json.tool
echo ""
echo ""

# 5. Status Code Distribution
echo "📈 5. HTTP Status Code Distribution"
echo "   GET /api/usage/status-codes"
curl -s "$BASE_URL/api/usage/status-codes" | python3 -m json.tool
echo ""
echo ""

# 6. Health Check
echo "💚 6. Analytics Health Check"
echo "   GET /api/usage/health"
curl -s "$BASE_URL/api/usage/health" | python3 -m json.tool
echo ""
echo ""

echo "✅ Analytics Demo Complete!"
echo ""
echo "📚 Additional Endpoints Available:"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   - JobRunr Dashboard: http://localhost:8000"
echo "   - API Docs: http://localhost:8080/v3/api-docs"
