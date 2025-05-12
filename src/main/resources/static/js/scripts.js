const modalInit = (elemId) => {
    return new BSN.Modal(elemId, { backdrop: true })
}

function toReadableName(str, separator = '_', concat= ' '){
    return str.split(separator).map(function(word){
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }).join(concat)
}

const getApi = (url) => {
    return fetch(url).then(response => response.json())
}

const postTextApi = (url, data) => {
    return fetch(url,
        {
            method: 'post',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        }).then(response => response.text())
}

function datePicker (defaultDate = "today") {
    let config = {
        defaultDate: defaultDate,
        altInput: true,
        altFormat: "d/m/Y",
        allowInput: true,
    }
    const fp = flatpickr("input[type=datetime-local]", config)

    return fp
}
/*************** date-utils ******************/
const getTodayDate = (increment = 0, delimiter = '-') => {
    let today = new Date()
    return convertDateTo(today, increment, delimiter)
}

const convertDateTo = (date, increment = 0, delimiter = '-') => {
    date.setDate(date.getDate() + increment)

    let dd = (date.getDate() + 100).toString().substring(1)
    let mm = (date.getMonth() + 101).toString().substring(1)
    let yyyy = date.getFullYear().toString()

    return [yyyy, mm, dd].join(delimiter)
}

function generateDecimalNumber(value, digit = 2) {
    return parseFloat(value.toFixed(digit))
}

function checkDecimalDigitLength(value) {
    if ((value % 1) != 0) return value.toString().split(".")[1].length
    return 0
}

function processDecimalNumbers(arr, columnList) {
    let result = arr.map(obj => {
        for(let prop of columnList){
            let val = obj[prop]

            obj[prop] = generateDecimalNumber(val, 8)
            obj[prop + 'Display'] = generateDecimalNumber(val)
        }

        return obj
    })

    return result
}