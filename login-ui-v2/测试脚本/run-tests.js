const { execSync } = require('child_process')
const fs = require('fs')
const path = require('path')

// 测试配置
const TEST_CONFIG = {
  // 测试环境
  environments: {
    dev: {
      baseUrl: 'http://localhost:3000',
      apiUrl: 'http://localhost:8080'
    },
    test: {
      baseUrl: 'http://test.example.com',
      apiUrl: 'http://test-api.example.com'
    },
    prod: {
      baseUrl: 'http://prod.example.com',
      apiUrl: 'http://api.example.com'
    }
  },
  
  // 浏览器配置
  browsers: ['chrome', 'firefox', 'edge'],
  
  // 测试套件
  testSuites: {
    smoke: [
      '01-login-test.js',
      '02-user-management-test.js'
    ],
    regression: [
      '01-login-test.js',
      '02-user-management-test.js',
      '03-role-management-test.js',
      '04-menu-management-test.js',
      '05-dept-management-test.js',
      '06-dict-management-test.js',
      '07-config-management-test.js',
      '08-notice-management-test.js',
      '09-log-management-test.js'
    ],
    performance: [
      '10-performance-test.js'
    ],
    compatibility: [
      '11-compatibility-test.js'
    ],
    security: [
      '12-security-test.js'
    ]
  },
  
  // 报告配置
  reports: {
    outputDir: 'test-reports',
    formats: ['html', 'json', 'junit']
  }
}

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m',
  white: '\x1b[37m'
}

// 日志函数
function log(message, color = 'white') {
  console.log(`${colors[color]}${message}${colors.reset}`)
}

function logSuccess(message) {
  log(`✓ ${message}`, 'green')
}

function logError(message) {
  log(`✗ ${message}`, 'red')
}

function logWarning(message) {
  log(`⚠ ${message}`, 'yellow')
}

function logInfo(message) {
  log(`ℹ ${message}`, 'blue')
}

// 创建报告目录
function createReportDir() {
  const reportDir = path.join(__dirname, TEST_CONFIG.reports.outputDir)
  if (!fs.existsSync(reportDir)) {
    fs.mkdirSync(reportDir, { recursive: true })
    logInfo(`创建报告目录: ${reportDir}`)
  }
  
  // 创建子目录
  const subDirs = ['screenshots', 'videos', 'html', 'json', 'junit']
  subDirs.forEach(dir => {
    const subDir = path.join(reportDir, dir)
    if (!fs.existsSync(subDir)) {
      fs.mkdirSync(subDir, { recursive: true })
    }
  })
}

// 检查环境
function checkEnvironment() {
  logInfo('检查测试环境...')
  
  try {
    // 检查Node.js版本
    const nodeVersion = process.version
    logInfo(`Node.js版本: ${nodeVersion}`)
    
    // 检查Cypress是否安装
    execSync('npx cypress version', { stdio: 'pipe' })
    logSuccess('Cypress已安装')
    
    // 检查测试文件
    const testFiles = fs.readdirSync(__dirname).filter(file => file.endsWith('-test.js'))
    logInfo(`找到 ${testFiles.length} 个测试文件`)
    
    return true
  } catch (error) {
    logError(`环境检查失败: ${error.message}`)
    return false
  }
}

// 运行测试套件
function runTestSuite(suiteName, options = {}) {
  const suite = TEST_CONFIG.testSuites[suiteName]
  if (!suite) {
    logError(`测试套件 '${suiteName}' 不存在`)
    return false
  }
  
  logInfo(`开始运行测试套件: ${suiteName}`)
  
  const {
    browser = 'chrome',
    environment = 'dev',
    headless = true,
    record = false,
    parallel = false
  } = options
  
  const env = TEST_CONFIG.environments[environment]
  if (!env) {
    logError(`环境配置 '${environment}' 不存在`)
    return false
  }
  
  try {
    // 构建Cypress命令
    let command = 'npx cypress run'
    
    // 指定测试文件
    const specPattern = suite.join(',')
    command += ` --spec "${specPattern}"`
    
    // 浏览器配置
    command += ` --browser ${browser}`
    
    // 环境变量
    command += ` --env baseUrl=${env.baseUrl},apiUrl=${env.apiUrl}`
    
    // 头部模式
    if (!headless) {
      command += ' --headed'
    }
    
    // 录制
    if (record) {
      command += ' --record'
    }
    
    // 并行执行
    if (parallel) {
      command += ' --parallel'
    }
    
    logInfo(`执行命令: ${command}`)
    
    // 执行测试
    const startTime = Date.now()
    execSync(command, { stdio: 'inherit', cwd: __dirname })
    const endTime = Date.now()
    const duration = (endTime - startTime) / 1000
    
    logSuccess(`测试套件 '${suiteName}' 执行完成，耗时: ${duration.toFixed(2)}秒`)
    return true
    
  } catch (error) {
    logError(`测试套件 '${suiteName}' 执行失败: ${error.message}`)
    return false
  }
}

// 运行所有测试
function runAllTests(options = {}) {
  logInfo('开始运行所有测试套件')
  
  const results = {}
  const suites = Object.keys(TEST_CONFIG.testSuites)
  
  for (const suite of suites) {
    if (options.suites && !options.suites.includes(suite)) {
      continue
    }
    
    results[suite] = runTestSuite(suite, options)
  }
  
  // 生成汇总报告
  generateSummaryReport(results)
  
  return results
}

// 生成汇总报告
function generateSummaryReport(results) {
  logInfo('生成汇总报告...')
  
  const summary = {
    timestamp: new Date().toISOString(),
    total: Object.keys(results).length,
    passed: Object.values(results).filter(r => r).length,
    failed: Object.values(results).filter(r => !r).length,
    results: results
  }
  
  // 保存JSON报告
  const reportFile = path.join(__dirname, TEST_CONFIG.reports.outputDir, 'summary.json')
  fs.writeFileSync(reportFile, JSON.stringify(summary, null, 2))
  
  // 生成HTML报告
  generateHtmlReport(summary)
  
  // 输出结果
  log('\n=== 测试结果汇总 ===', 'cyan')
  log(`总计: ${summary.total}`, 'white')
  logSuccess(`通过: ${summary.passed}`)
  logError(`失败: ${summary.failed}`)
  
  Object.entries(results).forEach(([suite, passed]) => {
    if (passed) {
      logSuccess(`${suite}: 通过`)
    } else {
      logError(`${suite}: 失败`)
    }
  })
  
  logInfo(`详细报告: ${reportFile}`)
}

// 生成HTML报告
function generateHtmlReport(summary) {
  const htmlTemplate = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>测试报告</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }
        .header { text-align: center; margin-bottom: 30px; }
        .summary { display: flex; justify-content: space-around; margin-bottom: 30px; }
        .summary-item { text-align: center; padding: 20px; border-radius: 8px; }
        .total { background-color: #e3f2fd; }
        .passed { background-color: #e8f5e8; }
        .failed { background-color: #ffebee; }
        .results { margin-top: 20px; }
        .result-item { padding: 10px; margin: 5px 0; border-radius: 4px; }
        .result-passed { background-color: #c8e6c9; }
        .result-failed { background-color: #ffcdd2; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>若依系统管理页面测试报告</h1>
            <p>生成时间: ${new Date(summary.timestamp).toLocaleString('zh-CN')}</p>
        </div>
        
        <div class="summary">
            <div class="summary-item total">
                <h3>${summary.total}</h3>
                <p>总计</p>
            </div>
            <div class="summary-item passed">
                <h3>${summary.passed}</h3>
                <p>通过</p>
            </div>
            <div class="summary-item failed">
                <h3>${summary.failed}</h3>
                <p>失败</p>
            </div>
        </div>
        
        <div class="results">
            <h3>详细结果</h3>
            ${Object.entries(summary.results).map(([suite, passed]) => `
                <div class="result-item ${passed ? 'result-passed' : 'result-failed'}">
                    <strong>${suite}</strong>: ${passed ? '✓ 通过' : '✗ 失败'}
                </div>
            `).join('')}
        </div>
    </div>
</body>
</html>`
  
  const htmlFile = path.join(__dirname, TEST_CONFIG.reports.outputDir, 'html', 'summary.html')
  fs.writeFileSync(htmlFile, htmlTemplate)
  logInfo(`HTML报告: ${htmlFile}`)
}

// 主函数
function main() {
  const args = process.argv.slice(2)
  const options = {}
  
  // 解析命令行参数
  for (let i = 0; i < args.length; i++) {
    const arg = args[i]
    switch (arg) {
      case '--suite':
      case '-s':
        options.suites = args[++i].split(',')
        break
      case '--browser':
      case '-b':
        options.browser = args[++i]
        break
      case '--env':
      case '-e':
        options.environment = args[++i]
        break
      case '--headed':
        options.headless = false
        break
      case '--help':
      case '-h':
        showHelp()
        return
    }
  }
  
  // 检查环境
  if (!checkEnvironment()) {
    process.exit(1)
  }
  
  // 创建报告目录
  createReportDir()
  
  // 运行测试
  const results = runAllTests(options)
  
  // 检查是否有失败的测试
  const hasFailures = Object.values(results).some(r => !r)
  if (hasFailures) {
    process.exit(1)
  }
}

// 显示帮助信息
function showHelp() {
  log('\n若依系统管理页面测试运行器', 'cyan')
  log('\n用法:', 'white')
  log('  node run-tests.js [选项]', 'white')
  log('\n选项:', 'white')
  log('  -s, --suite <suites>     指定测试套件', 'white')
  log('  -b, --browser <browser>  指定浏览器', 'white')
  log('  -e, --env <environment>  指定环境', 'white')
  log('  --headed                 显示浏览器界面', 'white')
  log('  -h, --help               显示帮助信息', 'white')
}

// 如果直接运行此脚本
if (require.main === module) {
  main()
}

module.exports = {
  runTestSuite,
  runAllTests,
  TEST_CONFIG
}