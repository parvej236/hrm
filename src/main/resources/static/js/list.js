function applyFilter(array) {
  let filterData = {}

  for (let elem of array) {
    filterData[elem] = createObj(elem)
  }

  let key = 'active-filter'
  let value = JSON.stringify(filterData)
  localStorage.setItem(key, value)

  filterCount()
}

function createObj(strVal) {
  let element = document.getElementsByName(strVal),
    notContainsTxt = document.getElementById('not-contains-'+strVal+'-txt').value,
    object = {}

  for (let i = 0; i < element.length; i++) {
    object[element[i].value] = element[i].checked

    if (element[i].checked) {
      document.getElementById(strVal + "Operator").value = element[i].value
      document.getElementById(strVal + "OperatorTxt").value = notContainsTxt
    }
  }

  object['inputTxt'] = notContainsTxt

  return object
}

function resetFilter() {
  let nodeList = document.querySelectorAll('#searchForm > input'),
    radioElems = document.querySelectorAll('#active-filter-modal-body input[type=radio]'),
    txtElems = document.querySelectorAll('#active-filter-modal-body input[type=text]')

  for (let node of nodeList) {
    node.value = ''
  }

  for (let radioBtn of radioElems) {
    radioBtn.checked = false
  }

  for (let inputTxt of txtElems) {
    inputTxt.value = ''
  }

  localStorage.removeItem('active-filter')
  localStorage.removeItem('filterCount')
  document.querySelector("#active-filter > span").innerText = 0

  setTimeout(function() {
    let modal = new BSN.Modal('#active-filter-modal')

    modal.hide()
  }, 1000)
}

function loadFilterFromLocalStorage(array) {
  for (let elem of array) {
    loadFromLocalStorage(elem)
  }
}

function filterCount() {
  let spanElem = document.querySelector("#active-filter > span"),
    radioElem = document.querySelectorAll("#active-filter-modal-body .form-check-input"),
    count = 0

  for (let i = 0; i < radioElem.length; i++) {
    if (radioElem[i].checked) {
      count++
    }
  }

  spanElem.innerText = count

  localStorage.setItem('filterCount', count.toString())
}

function loadFilterCount() {
  let filterCount = localStorage.getItem('filterCount'),
    spanElem = document.querySelector("#active-filter > span")

  spanElem.innerText = filterCount
}

function loadFromLocalStorage(keyVal) {
  let filterVal = JSON.parse(localStorage.getItem('active-filter'))

  if (filterVal[keyVal]['equals'] === true) {
    document.getElementById('equals-'+keyVal).checked = filterVal[keyVal]['equals']
  }
  if (filterVal[keyVal]['contains'] === true) {
    document.getElementById('contains-'+keyVal).checked = filterVal[keyVal]['contains']
  }
  if (filterVal[keyVal]["not-contains"] === true) {
    document.getElementById('not-contains-'+keyVal).checked = filterVal[keyVal]["not-contains"]
  }
  document.getElementById('not-contains-'+keyVal+'-txt').value = filterVal[keyVal]['inputTxt']
}

function submit(formId) {
  document.getElementById(formId).submit()
}

function applySettings() {
  let elements = document.querySelectorAll('#settings-modal-body .form-check.form-switch > input'),
    object = {}

  for (let i = 0; i < elements.length; i++) {
    if (elements[i].checked) {
      showHideColumn(elements[i].value, false)
      object[elements[i].value] = false
    } else {
      showHideColumn(elements[i].value, true)
    }
  }

  let key = 'tbl-settings'
  let value = JSON.stringify(object)
  localStorage.setItem(key, value)
}

function resetSettings() {
  let elements = document.querySelectorAll('.form-check.form-switch > input')
  for (let i = 0; i < elements.length; i++) {
    if (elements[i].checked) {
      elements[i].checked = false
      showHideColumn(elements[i].value, true)
    }
  }

  localStorage.removeItem('tbl-settings')

  setTimeout(function() {
    let modal = new BSN.Modal('#settings-modal')

    modal.hide()
  }, 1000)
}
function loadSettingsFromLocalStorage() {
  let settings = JSON.parse(localStorage.getItem('tbl-settings')),
    elements = document.querySelectorAll('.form-check.form-switch > input')
  for (let k in settings) {
    showHideColumn(k, settings[k])
    for (let i = 0; i < elements.length; i++) {
      if (elements[i].value === k) {
        elements[i].checked = true
      }
    }
  }
}

function showHideColumn(colNo, isDisplayCol) {
  let rows = document.getElementById('data-tbl').rows

  for (let row = 0; row < rows.length; row++) {
    let cols = rows[row].cells
    if (colNo >= 0 && colNo < cols.length) {
      cols[colNo].style.display = isDisplayCol ? '' : 'none'
    }
  }
}

function pagination() {
  const url = document.getElementById('searchForm').action

  let listSize = document.getElementById("list-size"),
    page = document.getElementById("page").value

  location.href=url+'?size=' + listSize.value + '&page=' + page
}

function firstPage() {
  document.getElementById("page").value = 0

  setTimeout(function() {
    submit('searchForm')
  }, 500)
}

function previousPage(currentPage) {
  if (currentPage > 0) {
    document.getElementById("page").value = currentPage - 1
  }

  setTimeout(function() {
    submit('searchForm')
  }, 500)
}

function nextPage(currentPage, lastPageNo) {

  if (currentPage < lastPageNo) {
    document.getElementById("page").value = currentPage + 1
  }

  setTimeout(function() {
    submit('searchForm')
  }, 500)
}

function lastPage(lasPageNo) {
  document.getElementById("page").value = lasPageNo

  setTimeout(function() {
    submit('searchForm')
  }, 500)
}

function download(fileContent, filename, fileType) {
  const blob = new Blob([fileContent], { type: fileType })

  let a = document.createElement("a")
  a.href = URL.createObjectURL(blob)
  a.setAttribute("download", filename)
  a.click()
}