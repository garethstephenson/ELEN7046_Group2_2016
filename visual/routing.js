var express = require('express');
var router = express.Router();

app.use(require('./controllers'));
router.use('/', require('./index'))
router.use('/config', require('./config'))
router.use('/data', require('./data'))