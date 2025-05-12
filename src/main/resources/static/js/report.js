const docDefinition = (content, styles, images, footer, title, subTitle,  pageOrientation = 'portrait', pageSize= 'A4') => {
    return  {
        pageSize,
        pageOrientation,
        // pageMargins: [72, 96, 50, 135],
        header: function(currentPage, pageCount, pageSize) {
            if (currentPage > 1)
                return [getHeaderCell(title, 1, 1, 'headerTitle'), getHeaderCell(subTitle, 1, 1, 'headerSubTitle')]
            else return ''
        },
        footer: function(currentPage, pageCount) {
            let page = 'Page ' + currentPage.toString() + ' of ' + pageCount
            return {
                columns: [
                    getHeaderCell(footer,1,1,'footer'),
                    getHeaderCell(page,1,1,'pageNumber')
                ]
            }
        },
        content,
        styles,
        images
    }
}

const getStyles = () => {
    return {
        QuantaLipi: {
            font: 'QuantaLipi.ttf',
            fontSize: 11
        },
        title: {
            alignment: 'center',
            fontSize: 15,
            bold: true
        },
        titleLeft: {
            alignment: 'left',
            fontSize: 20,
            width: '*',
            // margin: [20, 30, 15, 25],
            bold: true
        },
        subTitleLeft: {
            alignment: 'left',
            fontSize: 16,
            width: '*',
            // margin: [20, 30, 15, 25],
            bold: true
        },
        titleRight: {
            alignment: 'right',
            fontSize: 15,
            width: '*',
            // margin: [20, 30, 15, 25],
            bold: true
        },
        subTitle: {
            alignment: 'right',
            font: 'QuantaLipi.ttf',
            fontSize: 13,
            bold: true
        },
        duration: {
            alignment: 'center',
            fontSize: 11,
            bold: true
        },
        tableCell: {
            fontSize: 10,
            margin: [0, 5, 0, 15]
        },
        tableHeader: {
            bold: true,
            fontSize: 12,
            fillColor: '#d7d7d7',
        },
        headerTitle: {
            alignment: 'center',
            fontSize: 13,
            margin: [0, 9, 0, 0],
            bold: true
        },
        headerSubTitle: {
            alignment: 'center',
            font: 'QuantaLipi.ttf',
            fontSize: 13,
            bold: true
        },
        footer: {
            alignment: 'left',
            fontSize: 9,
            margin: [ 38, 15, 0, 0 ],
            bold: true
        },
        pageNumber: {
            alignment: 'right',
            fontSize: 9,
            margin: [ 0, 15, 40, 0 ],
            bold: true
        },
        rightAlign: {
            alignment: 'right'
        }
    }
}

const getImages = (src) => {
    return {logo: src}
}

const getHeader = (text = '') => {
    return text;
}

const getContent = (tables) => {
    let content = []

    for(let table of tables){
        content.push(table)
        content.push([{text:'\n'}])
    }

    return content
}

const getHeaderTable = (title, subTitle, duration, organization) => {
    return {
        columns: [
            {
                width: 60,
                image:'logo',
                fit: [50, 50]
            },
            [
                {
                    style:'titleLeft',
                    text: 'Quantum Foundation',
                },
                {
                    columns: [
                        {
                            style:'subTitleLeft',
                            text: 'Kendor',
                        }
                    ]
                }
            ],
            [
                {
                    style:'titleRight',
                    text: title,
                },
                {
                    columns: [
                        {
                            style:'subTitle',
                            text: duration + '\n' + subTitle,
                        }
                    ]
                }
            ]
        ]
    }
}

const getTable = (body, style, headerRows = 1) => {
    return {
        style,
        table: {
            headerRows: headerRows,
            body: body
        }
    }
}

const getHeaderCell = (text, colSpan = 1, rowSpan = 1, style = 'tableHeader') => {
    let cell = {text, style}

    if(colSpan > 1){
        cell.colSpan = colSpan
    }

    if(rowSpan > 1){
        cell.rowSpan = rowSpan
    }

    return cell
}

const getTableBody = (headers, data, footers) => {
    let body = []

    if(headers.length > 0) body.push(...headers)
    if(data.length > 0) body.push(...data)
    if(footers.length > 0) body.push(...footers)

    return body
}

const getHeaders = (headers) => {
    let result = []

    for(let header of headers){
        result.push(getHeaderCell(header))
    }

    return result
}

const getValuesFromObject = (obj, props) => {
    if (!obj || !props) return

    let result = []

    for(let prop of props){
        result.push(obj[prop])
    }

    return result
}

const findObjValuesFromArray = (arr, props) => {
    let result = []

    for(let dt of arr){
        let obj = {...dt}
        result.push(getValuesFromObject(obj, props))
    }

    return result;
}

const changeObjValueFromArray = (arr, props) => {
    let result = []

    for(let dt of arr){
        let obj = {...dt}

        for(let elem of props) {
            if(elem.formatNumber) {
                let txt = formatNumber(obj[elem.prop])
                obj[elem.prop] = getHeaderCell(txt,1,1, elem.style)
            }
            else {
                obj[elem.prop] = getHeaderCell(obj[elem.prop],1,1, elem.style)
            }
        }

        result.push(obj)
    }

    return result;
}

const mergedTwoArrayWithIndex = (arr1, obj1, arr2, obj2) => {
    let merged = [];
    let maxLen = arr1.length

    if(arr2.length > maxLen) {
        maxLen = arr2.length
    }

    for(let i=0; i < maxLen; i++) {
        let dt1 = arr1[i] == null ? obj1 : {...arr1[i]}
        let dt2 = arr2[i] == null ? obj2 : {...arr2[i]}
        merged.push({
            ...dt1,
            ...dt2
        });
    }
    return merged
}

const changeObjProperty = (obj, prefix) => {
    let result = {}
    for(let prop in obj){
        result[prefix + prop] = obj[prop] != null ? generateDecimalNumber(obj[prop]) : obj[prop]
    }
    return result
}

const changeArrObjProperty = (arr, prefix) => {
    let result = []
    for(let obj of arr){
        result.push(changeObjProperty(obj, prefix))
    }
    return result
}

const getQuantityRateAmountObjArray = () => {
    return [{ quantity : 0, rate : 0, amount : 0 }]
}

const changeDataFormat = (arr, props, formatter) => {
    let result = []

    for(let dt of arr) {
        let obj = {...dt}

        for(let elem of props) {
            if(obj[elem] != null){
                obj[elem] = formatter(obj[elem])
            } else {
                obj[elem] = ''
            }
        }

        result.push(obj)
    }

    return result
}

const formatNumber = (value) => {
    let num = 0

    if (typeof value === 'string' || value instanceof String) {
        num = parseFloat(value)
    } else {
        num = value
    }

    return  num.toLocaleString('en-US', {maximumFractionDigits:2})
}

function formatDecimalNumber(value, digit = 3) {
    let num = 0

    if (typeof value === 'string' || value instanceof String) {
        num = parseFloat(value)
    } else {
        num = value
    }
    return parseFloat(num.toFixed(digit))
}

const formatDate = (date) => {
    return convertDateToStr(new Date(date))
}

const addRowSpanToList = (arr, leftColumns, listName, props, type='pdf', rightColumns = []) => {
    let result = []

    for(let obj of arr) {
        let rs = []
        let leftObjData = getValuesFromObject(obj, leftColumns)
        let rightObjData = getValuesFromObject(obj, rightColumns)
        let data = obj[listName]

        if(data && data.length > 0) {
            for(let ob of leftObjData){
                if(typeof ob === "object") {
                    ob.rowSpan = data.length
                    rs.push(ob)
                } else {
                    if(type === 'pdf') {
                        rs.push(getHeaderCell(ob,1,data.length,'tableCell'))
                    } else {
                        rs.push(ob)
                    }
                }
            }

            let listData = findObjValuesFromArray(data, props)
            let cnt = 0
            for(let d of listData) {
                if(cnt == 0) {
                    rs.push(...d)
                    result.push(rs)
                } else {
                    let ar = []

                    for(let i =0 ; i < leftColumns.length ; i++){
                        ar.push('')
                    }

                    ar.push(...d)

                    for(let i =0 ; i < rightColumns.length ; i++){
                        ar.push('')
                    }

                    result.push(ar)
                }
                cnt ++
            }

            for(let ob of rightObjData){
                if(typeof ob === "object") {
                    ob.rowSpan = data.length
                    rs.push(ob)
                } else {
                    if(type === 'pdf') {
                        rs.push(getHeaderCell(ob,1,data.length,'tableCell'))
                    } else {
                        rs.push(ob)
                    }
                }
            }
        } else {
            for(let i =0 ; i < props.length ; i++){
                leftObjData.push('')
            }
            result.push(leftObjData)

            for(let i =0 ; i < props.length ; i++){
                rightObjData.push('')
            }
            result.push(rightObjData)
        }
    }

    return result
}

const addSerialNumberToList = (arr, key='SL') => {
    arr.map( (obj, index) => {
        obj[key] = index + 1
        return obj
    })
    return arr
}

const addTotalColumnToList = (arr, props, key='Total') => {
    for(let obj of arr){
        let total = 0
        for(let prop of props){
            let val = parseInt(obj[prop])
            if(val){
                total += val
            }
        }
        obj[key] = total
    }
    return arr
}

const generatePdfReport = (report, imageSrc, pageOrientation = 'portrait') => {
    let title = getHeader(report.header.title)
    let subTitle = getHeader(report.header.subTitle)
    let footer = getHeader(report.footer)
    let src = imageSrc
    let images = getImages(src)
    let styles = getStyles()

    let tables = report.tables
    let content = getContent(tables)
    let doc = docDefinition(content, styles, images, footer, title, subTitle, pageOrientation)
    return doc
}

const getTableData = (data, columns, headers, headerRows = 1, footer = []) => {
    let tableData = findObjValuesFromArray(data, columns)
    let body = getTableBody(headers, tableData, footer)
    let table = getTable(body,'tableCell', headerRows)
    return table
}

const getImageSource = (svgElement) => {
    const canvas = document.createElement('canvas');
    canvas.width = svgElement.clientWidth;
    canvas.height = svgElement.clientHeight;
    const canvasCtx = canvas.getContext('2d');
    canvasCtx.drawImage(svgElement, 0, 0);
    return  canvas.toDataURL('image/png');
}

const toDataURL = url => fetch(url)
    .then(response => response.blob())
    .then(blob => new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.onerror = reject
        reader.readAsDataURL(blob)
    }))

function getReport(result, pdfContents) {
    let report = {}
    report.header = result.header
    report.footer = result.footer

    let tables = []
    let headerTable = getHeaderTable(report.header.title, report.header.subTitle, report.header.duration, report.header.organization)
    tables.push(headerTable)

    tables.push(...pdfContents)

    report.tables = tables

    let logoImgUrl = '/resources/img/quantum_logo_head.png'

    toDataURL(logoImgUrl).then(base64Data => {
        let doc = generatePdfReport(report, base64Data, result.orientation)
        printPdf(doc)
    })
}

function printPdf(doc) {
    pdfMake.fonts = {
        'QuantaLipi.ttf': {
            normal: 'QuantaLipi.ttf',
            bold: 'QuantaLipi.ttf'
        },
        Roboto: {
            normal: 'Roboto-Regular.ttf',
            bold: 'Roboto-Medium.ttf',
            italics: 'Roboto-Italic.ttf',
            bolditalics: 'Roboto-MediumItalic.ttf'
        }
    }
    pdfMake.createPdf(doc,null, pdfMake.fonts, pdfMake.vfs).download("document.pdf")
}

function download(fileContent, filename, fileType) {
    const blob = new Blob([fileContent], { type: fileType })

    let a = document.createElement("a")
    a.href = URL.createObjectURL(blob)
    a.setAttribute("download", filename)
    a.click()
}

function downLoadCsv(data, columns) {
    let csv = getCsvData(data, columns)
    downLoadCsvContent(csv)
}

function downLoadCsvContent(content) {
    download(content, 'document.csv', 'text/csv')
}

const getCsvData = (data, columns) => {
    return Papa.unparse({
        "fields": columns,
        "data": data
    })
}

const getCsvHeader = (title, subTitle, duration, organization) => {
    let headerData = []

    headerData.push(['','', '', '',organization])
    headerData.push('\n')
    headerData.push(['','', '', '',title])
    headerData.push('\n')
    headerData.push(['','', '', '',subTitle])
    headerData.push('\n')
    headerData.push(['','', '', '',duration])
    headerData.push('\n\n')

    return  headerData
}

const getContentFromArr = (arr) => {
    let content = ''

    for(let str of arr){
        content += str
    }

    return content
}

function getCsvReport(result, contents) {
    let report = {}
    report.header = result.header

    let header = getCsvHeader(report.header.title, report.header.subTitle, report.header.duration, report.header.organization)
    let content = getContentFromArr(header) + contents
    downLoadCsvContent(content)
}

function getCsvReportFromArr(result, arr) {
    let content = getContentFromArr(arr)
    getCsvReport(result, content)
}

function getCsvReportFromData(result, data, labels) {
    let csv = getCsvData(data, labels)
    getCsvReport(result, csv)
}

function generateEmployeeListPdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.subTitle, 'landscape')
    const phoneReport = reportData.result['reportForPhone']
    const header = ['SL','Picture','Primary Information','Status & Date']
    if (phoneReport) {
        header.push('Phones')
    } else {
        header.push('Educational Qualification')
    }
    const rows = []
    const data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase();
    const statusMap = reportData.result['statusMap']

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.base64 !== null ? { image: item.base64, fit: [60, 60], } : {},
            {
                text: [
                    {text: `${item.name} (${item.employeeId})`, bold: true},
                    `\n${item.regCode}`,
                    `\n${item.phone}`,
                    {text: `\n${item.designation}`, bold: true},
                    `\n${item.branchName} - ${item.departmentName}`
                ]
            },
            [
                {text: statusMap[item.status], bold: true, decoration: 'underline'},
                `${item.joiningDate === null ? '' : "Joining: " + item.joiningDate}`,
                `${item.regularDate === null ? '' : "Regular: " + item.regularDate}`,
                `${item.promotionDate === null ? '' : "Promotion: " + item.promotionDate}`
            ]
        ]
        if (phoneReport) {
            row.push([
                {text: 'Primary Phone: ' + item.primaryPhone, bold: true},
                `${item.secondaryPhone === null ? '' : "Secondary Phone: " + item.secondaryPhone}`,
                `${item.phoneOffice === null ? '' : "Office: " + item.phoneOffice}`,
                `${item.phoneOthers === null ? '' : "Others: " + item.phoneOthers}`,
            ])
        } else {
            row.push([
                {text: item.examName, bold: true},
                `${item.subjectDepartment === null ? '' : "Regular: " + item.subjectDepartment}`,
                `${item.institution === null ? '' : "Promotion: " + item.institution}`
            ])
        }

        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','auto','33%','33%','*'])
    pdf.downloadPdf(filename)
}

function generateEmployeeListCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.subTitle)
    const phoneReport = reportData.result['reportForPhone']
    const header = ['SL','Name','Employee Id','Reg. No','Phone','Designation','Branch','Department','Status','Joining Date','Regular Date','Promotion Date']
    if (phoneReport) {
        header.push('Primary Phone')
        header.push('Secondary Phone')
        header.push('Phone Office')
        header.push('Phone Others')
    } else {
        header.push('Education')
        header.push('Subject/Department')
        header.push('Institution')
    }

    const rows = []
    let data = reportData.result['data']
    const statusMap = reportData.result['statusMap']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.name,
            item.employeeId,
            item.regCode,
            item.phone,
            item.designation,
            item.branchName,
            item.departmentName,
            statusMap[item.status],
            item.joiningDate,
            item.regularDate,
            item.promotionDate,
        ]
        if (phoneReport) {
            row.push(item.primaryPhone)
            row.push(item.secondaryPhone)
            row.push(item.phoneOffice)
            row.push(item.phoneOther)
        } else {
            row.push(item.examName)
            row.push(item.subjectDepartment)
            row.push(item.institutio)
        }
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateDesignationWiseEmployeePdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.subTitle, 'landscape')
    const header = []
    const headerRow1 = [
        {text: 'SL', rowSpan: 2, alignment: 'center'},
        {text: 'Designation', alignment: 'center', rowSpan: 2},
        {text: `Pro-Master`, alignment: 'center', colSpan: 3}, {}, {},
        {text: `Graduate`, alignment: 'center', colSpan: 3}, {}, {},
        {text: `Associate`, alignment: 'center', colSpan: 3}, {}, {},
        {text: `Total`, alignment: 'center', colSpan: 3}, {}, {},
    ]
    header.push(headerRow1)

    const headerRow2 = [
        '',
        '',
        {text: 'Male', alignment: 'center'},
        {text: 'Female', alignment: 'center'},
        {text: 'Total', alignment: 'center'},
        {text: 'Male', alignment: 'center'},
        {text: 'Female', alignment: 'center'},
        {text: 'Total', alignment: 'center'},
        {text: 'Male', alignment: 'center'},
        {text: 'Female', alignment: 'center'},
        {text: 'Total', alignment: 'center'},
        {text: 'Male', alignment: 'center'},
        {text: 'Female', alignment: 'center'},
        {text: 'Total', alignment: 'center'},
    ]
    header.push(headerRow2)

    const rows = []
    const data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.designation}`},
            {text: `${item.qpmMale}`},
            {text: `${item.qpmFemale}`},
            {text: `${item.qpmTotal}`},
            {text: `${item.qgMale}`},
            {text: `${item.qgFemale}`},
            {text: `${item.qgTotal}`},
            {text: `${item.qaMale}`},
            {text: `${item.qaFemale}`},
            {text: `${item.qaTotal}`},
            {text: `${item.totalMale}`},
            {text: `${item.totalFemale}`},
            {text: `${item.total}`},
        ]
        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','20%','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto'], 2)
    pdf.downloadPdf(filename)
}

function generateDesignationWiseEmployeeCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.subTitle)
    const header = ['SL','Designation','QPM Male','QPM Female','QPM Total','QG Male','QG Female','QG Total','QA Male','QA Female','QA Total','Total Male','Total Female','Total']
    const rows = []
    let data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.designation,
            item.qpmMale,
            item.qpmFemale,
            item.qpmTotal,
            item.qgMale,
            item.qgFemale,
            item.qgTotal,
            item.qaMale,
            item.qaFemale,
            item.qaTotal,
            item.totalMale,
            item.totalFemale,
            item.total,
        ]
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateEmployeeStatementMonthlyPdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.duration, 'landscape')
    const header = []
    const header1 = [
        {text: 'SL', alignment: 'center', rowSpan: 2},
        {text: 'Center/Branch/Cell', alignment: 'center', rowSpan: 2},
        {text: `Staff No. in Previous Month`, alignment: 'center', rowSpan: 2},
        {text: `Staff No. Joined in Current Month`, alignment: 'center', rowSpan: 2},
        {text: `Staff No. Transferred in Current Month`, alignment: 'center', rowSpan: 2},
        {text: `Staff No. Resigned in Current Month`, alignment: 'center', rowSpan: 2},
        {text: `Staff No. in Current Month`, alignment: 'center', colSpan: 4},{},{},{},
        {text: `Remarks`, alignment: 'center', rowSpan: 2},
    ]
    const header2 = [
        '','','','','','',
        {text: 'Regular', alignment: 'center'},
        {text: 'Irregular', alignment: 'center'},
        {text: `Others`, alignment: 'center'},
        {text: `Total`, alignment: 'center'},
        '',
    ]
    
    header.push(header1)
    header.push(header2)

    const rows = []
    const data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.branchName}`},
            {text: `${item.prevMonthCount}`, alignment: 'right'},
            {text: `${item.joinedInCurMonth}`, alignment: 'right'},
            {text: `${item.transferedInCurMonth}`, alignment: 'right'},
            {text: `${item.resignedInCurMonth}`, alignment: 'right'},
            {text: `${item.regularCount}`, alignment: 'right'},
            {text: `${item.irregularCount}`, alignment: 'right'},
            {text: `${item.othersCount}`, alignment: 'right'},
            {text: `${item.curMonthCount}`, alignment: 'right'},
            {text: ``},
        ]
        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','10%','10%','10%','10%','10%','10%','10%','10%','10%','auto'], 2)
    pdf.downloadPdf(filename)
}

function generateEmployeeStatementMonthlyCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.duration)
    const header = ['SL','Center/Branch/Cell','Staff No. in Previous Month','Staff No. Joined in Current Month','Staff No. Transferred in Current Month','Staff No. Resigned in Current Month','Regular Staff(s) in Current Month','Irregular Staff(s) in Current Month','Others Staff(s) in Current Month','Total Staff(s) in Current Month','Remarks']
    const rows = []
    let data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.branchName,
            item.prevMonthCount,
            item.joinedInCurMonth,
            item.transferedInCurMonth,
            item.resignedInCurMonth,
            item.regularCount,
            item.irregularCount,
            item.othersCount,
            item.curMonthCount,
        ]
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateEmployeeListDetailsPdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.duration, 'landscape')
    const header = []
    const headerRow1 = [
        {text: 'SL', alignment: 'center', rowSpan: 2},
        {text: 'Employee Id', alignment: 'center', rowSpan: 2},
        {text: 'Name', alignment: 'center', rowSpan: 2},
        {text: 'Designation', alignment: 'center', rowSpan: 2},
        {text: 'Status', alignment: 'center', rowSpan: 2},
        {text: 'C/B/C', alignment: 'center', rowSpan: 2},
        {text: 'O/C', alignment: 'center', rowSpan: 2},
        {text: 'Last C/B/C', alignment: 'center', rowSpan: 2},
        {text: 'Join Date', alignment: 'center', rowSpan: 2},
        {text: 'Regular Date', alignment: 'center', rowSpan: 2},
        {text: 'Promotion Date', alignment: 'center', rowSpan: 2},
        {text: 'Member Type', alignment: 'center', rowSpan: 2},
        {text: 'Date of Birth', alignment: 'center', rowSpan: 2},
        {text: 'Mobile', alignment: 'center', rowSpan: 2},
        {text: 'Home District', alignment: 'center', rowSpan: 2},
        {text: 'Blood Group', alignment: 'center', rowSpan: 2},
        {text: 'Academic Qualification', alignment: 'center', rowSpan: 2},
        {text: 'Marital Status', alignment: 'center', colSpan: 3}, {}, {},
        {text: 'Remarks', alignment: 'center', rowSpan: 2},
    ]
    const headerRow2 = [
        '','','','','','','','','','','','','','','','','',
        {text: 'M/S', alignment: 'center'},
        {text: 'Son', alignment: 'center'},
        {text: 'Daughter', alignment: 'center'},
        ''
    ]
    header.push(headerRow1)
    header.push(headerRow2)

    const rows = []
    const data = reportData.result['data']
    const statusMap = reportData.result['statusMap']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.employeeId != null ? item.employeeId : ''}`},
            {text: `${item.name}`},
            {text: `${item.designation}`},
            {text: `${statusMap[item.status]}`},
            {text: `${item.branchName}`},
            {text: `${item.departmentName !== null ? item.departmentName : ''}`},
            {text: `${item.lastCbc !== null ? item.lastCbc : ''}`},
            {text: `${item.joiningDate}`},
            {text: `${item.regularDate !== null ? item.regularDate : ''}`},
            {text: `${item.promotionDate !== null ? item.promotionDate : ''}`},
            {text: `${item.memberType === 'PRO_MASTER' ? 'QPM' : (item.memberType === 'GRADUATE' ? 'QG' : 'QA')}`},
            {text: `${item.dateOfBirth}`},
            {text: `${item.primaryPhone}`},
            {text: `${item.homeDistrict}`},
            {text: `${item.bloodGroup}`},
            {text: `${item.eduQualification}`},
            {text: `${item.maritalStatus === 'Married' ? 'M' : 'S'}`},
            {text: `${item.son}`},
            {text: `${item.daughter}`},
            {text: `${item.remarks}`},
        ]
        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto'], 2)
    pdf.downloadPdf(filename)
}

function generateEmployeeListDetailsCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.duration)
    const header = ['SL','Employee Id','Name','Designation','Status','C/B/C','O/C','Last C/B/C','Join Date','Regular Date','Promotion Date','Member Type','Date of Birth','Mobile','Home District','Blood Group','Academic Qualification','Marital Status','Son','Daughter','Remarks']
    const rows = []
    const data = reportData.result['data']
    const statusMap = reportData.result['statusMap']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.employeeId,
            item.name,
            item.designation,
            statusMap[item.status],
            item.branchName,
            item.departmentName !== null ? item.departmentName : '',
            item.lastCbc !== null ? item.lastCbc : '',
            item.joiningDate,
            item.regularDate !== null ? item.regularDate : '',
            item.promotionDate !== null ? item.promotionDate : '',
            item.memberType === 'PRO_MASTER' ? 'QPM' : (item.memberType === 'GRADUATE' ? 'QG' : 'QA'),
            item.dateOfBirth,
            item.primaryPhone,
            item.homeDistrict,
            item.bloodGroup,
            item.eduQualification,
            item.maritalStatus,
            item.son,
            item.daughter,
            item.remarks
        ]
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateAttendanceSheetPdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.subTitle, 'landscape')
    const columnList = reportData.result['columnList']
    const typeColumns = reportData.result['typeColumns']

    const rowWidth = ['auto','auto','auto','auto','auto','auto']

    const header = [
        {text: 'SL', alignment: 'center'},
        {text: 'Employee Id', alignment: 'center'},
        {text: 'Name', alignment: 'center'},
        {text: 'Designation', alignment: 'center'},
        {text: 'Join Date', alignment: 'center'},
    ]

    columnList.forEach(col => {
        header.push({text: `${col}`, alignment: 'center'})
        rowWidth.push('auto')
    })

    for([key, value] of Object.entries(typeColumns)) {
        header.push({text: `${value}`, alignment: 'center'})
        rowWidth.push('auto')
    }

    header.push({text: 'Total', alignment: 'center'})

    const rows = []
    const data = reportData.result['dto']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.employeeId}`},
            {text: `${item.employeeName}`},
            {text: `${item.empDesignationStr}`},
            {text: `${item.joinDate}`},
        ]
        item.attendanceTypeList.forEach(type => row.push({text: `${type}`, alignment: 'center'}))
        item.totalAttendance.forEach(att => row.push({text: `${att}`, alignment: 'right'}))
        row.push({text: `${item.total}`})

        rows.push(row)
    }

    pdf.addTable(header, rows, rowWidth)
    pdf.downloadPdf(filename)
}

function generateAttendanceSheetCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.subTitle)
    const columnList = reportData.result['columnList']
    const typeColumns = reportData.result['typeColumns']
    const header = ['SL','Employee Id','Name','Designation','Join Date']

    columnList.forEach(col => header.push(col))
    for([key, value] of Object.entries(typeColumns)) {
        header.push(value)
    }
    header.push('Total')

    const rows = []
    const data = reportData.result['dto']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.employeeId,
            item.employeeName,
            item.empDesignationStr,
            item.joinDate,
        ]
        item.attendanceTypeList.forEach(type => row.push(type))
        item.totalAttendance.forEach(att => row.push(att))
        row.push(item.total)
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateAttendanceSummaryPdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.subTitle, 'landscape')
    const typeColumns = reportData.result['typeColumns']

    const rowWidth = ['auto','auto','auto','auto','auto','auto']

    const header = [
        {text: 'SL', alignment: 'center'},
        {text: 'Employee Id', alignment: 'center'},
        {text: 'Name', alignment: 'center'},
        {text: 'Designation', alignment: 'center'},
        {text: 'Join Date', alignment: 'center'},
    ]

    for([key, value] of Object.entries(typeColumns)) {
        header.push({text: `${value}`, alignment: 'center'})
        rowWidth.push('auto')
    }

    header.push({text: 'Total', alignment: 'center'})

    const rows = []
    const data = reportData.result['dto']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.employeeId}`},
            {text: `${item.employeeName}`},
            {text: `${item.empDesignationStr}`},
            {text: `${item.joinDate}`},
        ]
        item.totalAttendance.forEach(att => row.push({text: `${att}`}))
        row.push({text: `${item.total}`})

        rows.push(row)
    }

    pdf.addTable(header, rows, rowWidth)
    pdf.downloadPdf(filename)
}

function generateAttendanceSummaryCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.subTitle)
    const typeColumns = reportData.result['typeColumns']
    const header = ['SL','Employee Id','Name','Designation','Join Date']

    for([key, value] of Object.entries(typeColumns)) {
        header.push(value)
    }
    header.push('Total')

    const rows = []
    const data = reportData.result['dto']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.employeeId,
            item.employeeName,
            item.empDesignationStr,
            item.joinDate,
        ]
        item.totalAttendance.forEach(att => row.push(att))
        row.push(item.total)
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function generateLeavePdfReport(reportData) {
    const title = reportData.header.title.trim()
    const pdf = new PDF(title, reportData.header.subTitle, 'landscape')
    const header = []
    const headerRow1 = [
        {text: 'SL', rowSpan: 2, alignment: 'center'},
        {text: 'Employee Id', alignment: 'center', rowSpan: 2},
        {text: `Name`, alignment: 'center', rowSpan: 2},
        {text: `Registration`, alignment: 'center', rowSpan: 2},
        {text: `Designation`, alignment: 'center', rowSpan: 2},
        {text: `Casual Leave`, alignment: 'center', colSpan: 3}, {}, {},
        {text: `YR Leave`, alignment: 'center', colSpan: 4}, {}, {}, {},
    ]
    header.push(headerRow1)

    const headerRow2 = [
        '',
        '',
        '',
        '',
        '',
        {text: 'Mature', alignment: 'center'},
        {text: 'Taken', alignment: 'center'},
        {text: 'Balance', alignment: 'center'},
        {text: 'Mature', alignment: 'center'},
        {text: 'Taken', alignment: 'center'},
        {text: 'Encash', alignment: 'center'},
        {text: 'Balance', alignment: 'center'},
    ]
    header.push(headerRow2)

    const rows = []
    const data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase();

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.employeeId}`},
            {text: `${item.name}`},
            {text: `${item.regCode}`},
            {text: `${item.designation}`},
            {text: `${item.matureCl}`},
            {text: `${item.takenCl}`},
            {text: `${item.balanceCl}`},
            {text: `${item.matureYr}`},
            {text: `${item.takenYr}`},
            {text: `${item.encashYr}`},
            {text: `${item.balanceYr}`},
        ]
        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto','auto'], 2)
    pdf.downloadPdf(filename)
}

function generateLeaveCsvReport(reportData) {
    const title = reportData.header.title.trim()
    const csv = new CSV(title, reportData.header.subTitle)
    const header = ['SL','Employee Id','Name','Registration','Designation','Mature CL','Taken CL','Balance CL','Mature YR','Taken YR','Encash YR','Balance YR']
    const rows = []
    let data = reportData.result['data']
    const filename = title.replace(/\s+/g, '_').toLowerCase().concat('.csv');

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.employeeId,
            item.name,
            item.regCode,
            item.designation,
            item.matureCl,
            item.takenCl,
            item.balanceCl,
            item.matureYr,
            item.takenYr,
            item.encashYr,
            item.balanceYr,
        ]
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

function exportIdCardsDataAsPdf(reportData) {
    const title = `Employee's Id Card Info`
    const pdf = new PDF(title, `Id Card Info`)
    const header = ['SL','Employee Id','Name','Designation','Branch','Department','Blood Group']
    const rows = []
    let data = reportData['data']
    const filename = `id-card-data.pdf`;

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            {text: `${item.employeeId}`},
            {text: `${item.employeeName}`},
            {text: `${item.designation}`},
            {text: `${item.branchName}`},
            {text: `${item.departmentName}`},
            {text: `${item.bloodGroup}`},
        ]
        rows.push(row)
    }

    pdf.addTable(header, rows, ['auto','auto','auto','auto','auto','auto','auto'])
    pdf.downloadPdf(filename)
}

function exportIdCardsDataAsCsv(reportData) {
    const title = `Employee's Id Card Info`
    const csv = new CSV(title, `Id Card Info`)
    const header = ['SL','Employee Id','Name','Designation','Branch','Department','Blood Group']
    const rows = []
    let data = reportData['data']
    const filename = `id-card-data.csv`;

    for (const [index, item] of data.entries()) {
        let row = [
            index + 1,
            item.employeeId,
            item.employeeName,
            item.designation,
            item.branchName,
            item.departmentName,
            item.bloodGroup,
        ]
        rows.push(row)
    }

    csv.addTable(header, rows)
    csv.downloadCsv(filename)
}

const generateDailyAttendanceViewPdf = (reportData) => {
    const title = reportData.header.title.trim()
    const filename = title.replace(/\s+/g, '_').toLowerCase()
    const pdf = new PDF(title, reportData.header.subTitle+'\n'+reportData.header.duration)
    const summary = reportData.result['summary']
    const dataList = reportData.result['data']

    const summaryHeader = [
        {text: `Timely: `, bold: true, style: 'tableExample'},
        {text: `${summary.timely}`, style: 'tableExample'}
    ]

    let summaryRows = [
        [
            {text: `Delay: `, bold: true},
            {text: `${summary.delay}`}
        ],
        [
            {text: `Movement: `, bold: true},
            {text: `${summary.movement}`}
        ],
        [
            {text: `Leave: `, bold: true},
            {text: `${summary.leave}`}
        ],
        [
            {text: `Absent: `, bold: true},
            {text: `${summary.absent}`}
        ],
    ]

    pdf.addTable(summaryHeader, summaryRows, ['60%', '40%'])
    pdf.addContent({ text: ``})

    for(const [key, value] of Object.entries(dataList)) {
        let header = [
            {text: `Name`, alignment: 'center'},
        ]

        if (key !== 'Absent') {
            header.push({text: `${key === 'Movement' || key === 'Leave' ? 'From' : 'In Time'}`, alignment: 'center'})
            header.push({text: `${key === 'Movement' || key === 'Leave' ? 'To' : 'Out Time'}`, alignment: 'center'})
        }

        let rows = []
        pdf.addContent({ text: `${key}`, style: 'sectionHeader'})

        for (const [index, item] of value.entries()) {
            if (item.name !== 'Total') {
                let row = []
                if (key === 'Absent') {
                    row = [
                        {text: `${item.name !== null ? item.name : ''}`}
                    ]
                } else {
                    row = [
                        {text: `${item.name !== null ? item.name : ''}`},
                        {text: `${item.inTime !== null ? item.inTime : ''}`, alignment: 'center'},
                        {text: `${item.outTime !== null ? item.outTime : ''}`, alignment: 'center'},
                    ]
                }
                rows.push(row)
            } else if (item.name === 'Total') {
                if (key !== 'Absent') {
                    const row = [
                        {text: `${item.name !== null ? item.name : ''}`, alignment: 'center', bold: true},
                        {text: `${item.inTime !== null ? item.inTime : ''}`, colspan:2, alignment: 'center', bold: true},
                        {},
                    ]
                    rows.push(row)
                }
            }
        }

        pdf.addTable(header, rows, ['50%','25%','25%'])
        pdf.addContent({ text: ``})
    }

    pdf.downloadPdf(filename)
}