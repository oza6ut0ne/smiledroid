const path = require('path');
const TerserPlugin = require("terser-webpack-plugin");

const isDev = process.env.NODE_ENV === 'development';

/** @type import('webpack').Configuration */
const baseConfig = {
  mode: isDev ? 'development' : 'production',
  resolve: {
    extensions: ['.js', '.ts', '.jsx', '.tsx', '.json'],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        loader: 'ts-loader',
      },
    ],
  },
  devtool: isDev ? 'inline-cheap-module-source-map' : false,
};

/** @type import('webpack').Configuration */
const renderer = {
  ...baseConfig,
  target: 'web',
  entry: path.join(__dirname, 'src/ts/renderer'),
  output: {
    filename: 'bundle.js',
    libraryTarget: 'umd',
    library: 'main',
    path: path.join(__dirname, '../../app/src/main/assets/dist/js'),
    hashFunction: "sha256"
  },
  optimization: {
    minimize: !isDev,
    minimizer: [new TerserPlugin({
      terserOptions: {
        keep_fnames: true,
      },
    })]
  }
};

module.exports = [renderer];
