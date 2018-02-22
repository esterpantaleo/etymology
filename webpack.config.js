const path = require('path');
const paths = {
    src: path.join(__dirname, 'src'),
    dist: path.join(__dirname, 'dist'),
    data: path.join(__dirname, 'data'),
    img: path.join(__dirname, 'src/img'),
    css: path.join(__dirname, 'src/css'),
    html: path.join(__dirname, 'src/html'),
    modules: path.join(__dirname, 'node_modules')
}

var webpack = require('webpack');

const CopyWebpackPlugin = require('copy-webpack-plugin')

module.exports = {
    context: paths.src,
    entry: './js/app.js',
    output: {
	filename: 'bundle.js',
	path: paths.dist,
	publicPath: 'dist'
    },
    plugins: [
	new webpack.ProvidePlugin({
	    $: 'jquery',
	    jQuery: 'jquery',
	    "window.jQuery": "jquery'",
	    "window.$": "jquery",
	    hopscotch: 'hopscotch'
	}),
	new CopyWebpackPlugin([
	    {
		from: paths.html,
		to: paths.dist
	    }
	]),
	new CopyWebpackPlugin([
	    {
		from: paths.img,
		to: paths.dist + '/img'
	    }
	]),
	new CopyWebpackPlugin([
            {
                from: paths.css,
                to: paths.dist
            }
        ])
    ],
    module: {
	rules: [
	    { test: /\.svg$/, loader: 'svg-loader' },
	    { test: /\.html$/, use: ['html-loader'], exclude: /node_modules/ },
	    { test: /\.css$/, use: ['style-loader', 'css-loader'], include: /node_modules/ },
	    { test: /\.js$/, exclude: /node_modules/, use: ['babel-loader'] },
	    { test: /\.(jpe?g|png|gif)$/i, loader: "file-loader", query: { name: '[name].[ext]', outputPath: 'img/' } },
	    { test: /\.(tab|csv)$/, loader: 'raw-loader' }
	]
    },
    devServer: {
	contentBase: paths.dist,
	compress: true,
	port: '4800',
	stats: 'errors-only',
	historyApiFallback: true
    },
    resolve : {
	alias: {
	    // bind to modules;
	    modules: paths.modules
	}
    },
    node: {
	// eslint-disable-next-line camelcase
	child_process: 'empty',
	dgram: 'empty',
	fs: 'empty',
	net: 'empty',
	tls: 'empty'
    }
};
