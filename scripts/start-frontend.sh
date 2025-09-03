#!/bin/bash

# PDF Generator Platform - Start Frontend Script
# This script starts the React frontend application

set -e

echo "🚀 Starting PDF Generator Frontend..."

# Navigate to frontend directory
cd frontend

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js first."
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm first."
    exit 1
fi

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Start the development server
echo "🚀 Starting React development server..."
echo ""
echo "📋 Application will be available at:"
echo "   Frontend: http://localhost:3000"
echo ""

npm start