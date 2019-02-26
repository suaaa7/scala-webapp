const path = require('path');

module.exports = {
  entry: './app/views/index.js',
  mode: 'none',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, '../public/javascripts')
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: [
                ['@babel/preset-env', { 'modules': false }],
                '@babel/preset-react'
              ]
            }
          }
        ],
        exclude: /node_modules/,
      }
    ]
  },
  devtool: 'source-map'
};
