const statusPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_STATUS_INFO}]]*/ "/add-status-info"
const statusDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_STATUS_INFO}]]*/ "/delete-status-info"
const promotionPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_PROMOTION_INFO}]]*/ "/add-promotion-info"
const promotionDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_PROMOTION_INFO}]]*/ "/delete-promotion-info"
const transferPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_TRANSFER_INFO}]]*/ "/add-transfer-info"
const transferDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_TRANSFER_INFO}]]*/ "/delete-transfer-info"
const preJoinPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_PRE_JOINING_INFO}]]*/ "/add-pre-joining-info"
const preJoinDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_PRE_JOINING_INFO}]]*/ "/delete-pre-joining-info"
const khedmotaionPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_KHEDMOTAION_INFO}]]*/ "/add-khedmotaion-info"
const khedmotaionDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_KHEDMOTAION_INFO}]]*/ "/delete-khedmotaion-info"
const educationPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_EDUCATION_INFO}]]*/ "/add-educational-info"
const educationDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_EDUCATION_INFO}]]*/ "/delete-educational-info"
const experiencePostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_EXPERIENCE_INFO}]]*/ "/add-experience-info"
const experienceDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_EXPERIENCE_INFO}]]*/ "/delete-experience-info"
const trainingPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_TRAINING_INFO}]]*/ "/add-training-info"
const trainingDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_TRAINING_INFO}]]*/ "/delete-training-info"
const extraQualificationPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_EXTRA_QUALIFICATION_INFO}]]*/ "/add-extra-qualification-info"
const extraQualificationDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_EXTRA_QUALIFICATION_INFO}]]*/ "/delete-extra-qualification-info"
const languageSkillPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_LANGUAGE_SKILL_INFO}]]*/ "/add-language-skill-info"
const languageSkillDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_LANGUAGE_SKILL_INFO}]]*/ "/delete-language-skill-info"
const familyPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_FAMILY_INFO}]]*/ "/add-family-info"
const familyDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_FAMILY_INFO}]]*/ "/delete-family-info"
const punishmentPostOrPutUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).ADD_PUNISHMENT_INFO}]]*/ "/add-punishment-info"
const punishmentDeleteUrl = /*[[${T(bd.org.quantum.hrm.common.Routes).DELETE_PUNISHMENT_INFO}]]*/ "/delete-punishment-info"

function postStatus() {
    const statusTo = document.getElementById('statusTo').value
    const status = {
        employee: {id: document.getElementById('rowId').value},
        dateFrom: document.getElementById('dateFrom').value,
        statusFrom: document.getElementById('statusFrom').value,
        statusTo: document.getElementById('statusTo').value,
        comments: document.getElementById('comments').value,
    }

    postTextApi(statusPostOrPutUrl, status)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                document.getElementById('status').value = statusTo
                loadStatusList()
                setTimeout(addStatusRow, 100)
            }
        })
}

function putStatus(index, isLast) {
    const statusTo = document.getElementById(`statusTo_${index}`).value
    const status = {
        id: document.getElementById(`statusId_${index}`).value,
        employee: {id: document.getElementById(`rowId`).value},
        dateFrom: document.getElementById(`dateFrom_${index}`).value,
        statusFrom: document.getElementById(`statusFrom_${index}`).value,
        statusTo: document.getElementById(`statusTo_${index}`).value,
        comments: document.getElementById(`comments_${index}`).value,
    }

    postTextApi(statusPostOrPutUrl, status)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                if (isLast && statusTo !== null && statusTo !== undefined) {
                    document.getElementById('status').value = statusTo
                }
                loadStatusList()
            }
        })
}

function deleteStatus(index, isLast) {
    const status = document.getElementById(`statusTo_${index - 1}`).value
    const statusId = document.getElementById(`statusId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(statusDeleteUrl + '?id=' + statusId)
        if (isLast && status !== null && status !== undefined) {
            document.getElementById('status').value = status
        }
        setTimeout(loadStatusList, 100)
    }
}

function editStatus(index) {
    const view = document.querySelectorAll(`.status-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.status-update-${index}`)
    const statusInfoId = document.getElementById(`statusId_${index}`).value
    const url = `/get-status-info?id=${statusInfoId}`

    fetch(url).then(response => response.json()).then(statusInfo => {
        updateView.forEach(el => {
            el.classList.remove('d-none')
            el.classList.add('d-block')
            if (el.id === `dateFrom_${index}`) {
                let formattedDate
                if (el.value.includes('-')) {
                    const dateArr = el.value.split('-')
                    formattedDate = dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0]
                } else {
                    formattedDate = el.value
                }
                applyDatepicker(el, formattedDate)
            }
            if (el.id === `statusFrom_${index}`) {
                document.getElementById(`statusFrom_${index}`).value = statusInfo.statusFrom
            }
            if (el.id === `statusTo_${index}`) {
                document.getElementById(`statusTo_${index}`).value = statusInfo.statusTo
            }
            if (el.id === `comments_${index}`) {
                document.getElementById(`comments_${index}`).value = statusInfo.comments
            }
        })
    })
}

function restoreStatus(index) {
    const view = document.querySelectorAll(`.status-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.status-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addStatusRow() {
    let options = `<option value="0">-Please Select-</option>`
    for (const [key, value] of Object.entries(statusMap)) {
        options += `<option value="${key}">${value}</option>`
    }

    const manageBtn = document.querySelector('.manage-status-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'statusDynamicRow')
    const row = `
                    <td>
                        <input type="text" placeholder="DD/MM/YYYY" id="dateFrom" class="form-control">
                    </td>
                    <td>
                        <select id="statusFrom" class="form-select">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="statusTo" class="form-select">
                            ${options}
                        </select>
                    </td>
                    <td><input id="comments" class="form-control" ></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#statusAccordion" onclick="postStatus()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#statusAccordion" onclick="removeStatusRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#statusTable tbody')
    body.appendChild(rowEl)
    const dateFrom = document.querySelector('#dateFrom')
    setTimeout(applyDatepicker, 100, dateFrom, dateFrom.value)
}

function removeStatusRow() {
    document.getElementById('statusDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-status-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadStatusList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-status-info?employeeId=' + empId
    const tableData = document.querySelector('#statusTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    let options = `<option value="0">-Please Select-</option>`
    for (const [key, value] of Object.entries(statusMap)) {
        options += `<option value="${key}">${value}</option>`
    }

    fetch(url).then(response => response.json()).then(data => {
        const statusSize = data.length
        data.forEach((status, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext status-view-${index} d-block">
                            ${status.dateFrom}
                        </label>
                        <input type="hidden" id="statusId_${index}" value="${status.id}">
                        <input type="text" id="dateFrom_${index}" class="form-control status-update-${index} d-none" value="${status.dateFrom}">
                    </td>
                    <td>
                        <label class="form-control-plaintext status-view-${index} d-block">
                            ${statusMap[status.statusFrom] !== undefined ? statusMap[status.statusFrom] : ''}
                        </label>
                        <select id="statusFrom_${index}" class="form-select status-update-${index} d-none">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext status-view-${index} d-block">
                            ${statusMap[status.statusTo]}
                        </label>
                        <select id="statusTo_${index}" class="form-select status-update-${index} d-none">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext status-view-${index} d-block">
                            ${status.comments || ''}
                        </label>
                        <input type="text" id="comments_${index}" class="form-control status-update-${index} d-none" value="${status.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary status-view-${index} d-inline-block d-block" href="#statusAccordion" onclick="editStatus(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary status-update-${index} d-inline-block d-none" href="#statusAccordion" onclick="putStatus(${index}, ${statusSize === (index + 1)})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp;&nbsp; | &nbsp;&nbsp;&nbsp;</span>
                            <a class="link-primary status-view-${index} d-inline-block d-block" href="#statusAccordion" onclick="deleteStatus(${index}, ${statusSize === (index + 1)})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary status-update-${index} d-inline-block d-none" href="#statusAccordion" onclick="restoreStatus(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postPromotion() {
    const promotTo = document.getElementById('designationTo')
    const promotion = {
        employee: {id: document.getElementById('rowId').value},
        dateFrom: document.getElementById('promoDateFrom').value,
        type: document.getElementById('type').value,
        designationFrom: document.getElementById('designationFrom').value,
        designationTo: document.getElementById('designationTo').value,
        comments: document.getElementById('comments').value,
    }

    postTextApi(promotionPostOrPutUrl, promotion)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                const options = promotTo.options
                const selectedOption = options[options.selectedIndex]
                document.getElementById('designation').value = selectedOption.dataset.rowid
                document.getElementById('designationName').value = promotTo.value
                loadPromotionList()
                setTimeout(addPromotionRow, 100)
            }
        })
}

function putPromotion(index, isLast) {
    const promotTo = document.getElementById(`designationTo_${index}`)
    const promotion = {
        id: document.getElementById(`promotionId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        dateFrom: document.getElementById(`promoDateFrom_${index}`).value,
        type: document.getElementById(`type_${index}`).value,
        designationFrom: document.getElementById(`designationFrom_${index}`).value,
        designationTo: document.getElementById(`designationTo_${index}`).value,
        comments: document.getElementById(`degComments_${index}`).value,
    }

    postTextApi(promotionPostOrPutUrl, promotion)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                if (isLast && promotTo != null) {
                    const options = promotTo.options
                    const selectedOption = options[options.selectedIndex]
                    document.getElementById('designation').value = selectedOption.dataset.rowid
                    document.getElementById('designationName').value = promotTo.value
                }
                loadPromotionList()
            }
        })
}

function deletePromotion(index, isLast) {
    const designationTo = document.getElementById(`designationTo_${index - 1}`)
    const promotionId = document.getElementById(`promotionId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(promotionDeleteUrl + '?id=' + promotionId)
        if (isLast && designationTo !== null) {
            const options = designationTo.options
            const selectedOption = options[options.selectedIndex]
            document.getElementById('designation').value = selectedOption.dataset.rowid
            document.getElementById('designationName').value = designationTo.value
        }
        setTimeout(loadPromotionList, 100)
    }
}

function editPromotion(index) {
    const view = document.querySelectorAll(`.promotion-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.promotion-update-${index}`)
    const promotionId = document.getElementById(`promotionId_${index}`).value
    const url = `/get-promotion-info?id=${promotionId}`

    fetch(url).then(response => response.json()).then(promotionInfo => {
        updateView.forEach(el => {
            el.classList.remove('d-none')
            el.classList.add('d-block')
            if (el.id === `promoDateFrom_${index}`) {
                let formattedDate
                if (el.value.includes('-')) {
                    const dateArr = el.value.split('-')
                    formattedDate = dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0]
                } else {
                    formattedDate = el.value
                }
                applyDatepicker(el, formattedDate)
            }
            if (el.id === `type_${index}`) {
                document.getElementById(`type_${index}`).value = promotionInfo.type
            }
            if (el.id === `designationFrom_${index}`) {
                document.getElementById(`designationFrom_${index}`).value = promotionInfo.designationFrom
            }
            if (el.id === `designationTo_${index}`) {
                document.getElementById(`designationTo_${index}`).value = promotionInfo.designationTo
            }
            if (el.id === `degComments_${index}`) {
                document.getElementById(`degComments_${index}`).value = promotionInfo.comments
            }
        })
    })
}

function restorePromotion(index) {
    const view = document.querySelectorAll(`.promotion-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.promotion-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addPromotionRow() {
    let options = `<option value="0">-Please Select-</option>`
    for (const [key, value] of Object.entries(designationMap)) {
        options += `<option data-rowid="${key}" value="${value}">${value}</option>`
    }

    let typeOptions = ''
    for (const [key, value] of Object.entries(typeMap)) {
        typeOptions += `<option value="${key}">${value}</option>`
    }

    const manageBtn = document.querySelector('.manage-promotion-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'promotionDynamicRow')
    const row = `
                    <td>
                        <input type="text" placeholder="DD/MM/YYYY" id="promoDateFrom" class="form-control">
                    </td>
                    <td>
                        <select id="type" class="form-select">
                            ${typeOptions}
                        </select>
                    </td>
                    <td>
                        <select id="designationFrom" class="form-select">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="designationTo" class="form-select">
                            ${options}
                        </select>
                    </td>
                    <td><input id="comments" class="form-control" ></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#promotionAccordion" onclick="postPromotion()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#promotionAccordion" onclick="removePromotionRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#promotionTable tbody')
    body.appendChild(rowEl)
    const dateFrom = document.querySelector('#promoDateFrom')
    setTimeout(applyDatepicker, 100, dateFrom, dateFrom.value)
}

function removePromotionRow() {
    document.getElementById('promotionDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-promotion-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadPromotionList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-promotion-info?employeeId=' + empId
    const tableData = document.querySelector('#promotionTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    let options = `<option value="0">-Please Select-</option>`
    for (const [key, value] of Object.entries(designationMap)) {
        options += `<option data-rowid="${key}" value="${value}">${value}</option>`
    }

    let typeOptions = ''
    for (const [key, value] of Object.entries(typeMap)) {
        typeOptions += `<option value="${key}">${value}</option>`
    }

    fetch(url).then(response => response.json()).then(data => {
        const rowSize = data.length
        data.forEach((promo, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext promotion-view-${index} d-block">
                            ${promo.dateFrom}
                        </label>
                        <input type="hidden" id="promotionId_${index}" value="${promo.id}">
                        <input type="text" id="promoDateFrom_${index}" class="form-control promotion-update-${index} d-none" value="${promo.dateFrom}">
                    </td>
                    <td>
                        <label class="form-control-plaintext promotion-view-${index} d-block">
                            ${typeMap[promo.type] !== undefined ? typeMap[promo.type] : ''}
                        </label>
                        <select id="type_${index}" class="form-select promotion-update-${index} d-none">
                            ${typeOptions}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext promotion-view-${index} d-block">
                            ${promo.designationFrom}
                        </label>
                        <select id="designationFrom_${index}" class="form-select promotion-update-${index} d-none">
                            <option value="0">-Please Select-</option>
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext promotion-view-${index} d-block">
                            ${promo.designationTo}
                        </label>
                        <select id="designationTo_${index}" class="form-select promotion-update-${index} d-none">
                            <option value="0">-Please Select-</option>
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext promotion-view-${index} d-block">
                            ${promo.comments || ''}
                        </label>
                        <input type="text" id="degComments_${index}" class="form-control promotion-update-${index} d-none" value="${promo.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary promotion-view-${index} d-inline-block d-block" href="#promotionAccordion" onclick="editPromotion(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary promotion-update-${index} d-inline-block d-none" href="#promotionAccordion" onclick="putPromotion(${index}, ${rowSize === (index + 1)})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary promotion-view-${index} d-inline-block d-block" href="#promotionAccordion" onclick="deletePromotion(${index}, ${rowSize === (index + 1)})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary promotion-update-${index} d-inline-block d-none" href="#promotionAccordion" onclick="restorePromotion(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postTransfer() {
    const branchFrom = document.getElementById('branchFrom')
    const branchTo = document.getElementById('branchTo')
    const deptFrom = document.getElementById('departmentFrom')
    const deptTo = document.getElementById('departmentTo')
    const transfer = {
        employee: {id: document.getElementById('rowId').value},
        transferDate: document.getElementById('transferDate').value,
        branchFrom: branchFrom.value,
        branchFromName: branchFrom.options[branchFrom.selectedIndex].textContent,
        departmentFrom: deptFrom.value,
        departmentFromName: deptFrom.options[deptFrom.selectedIndex].textContent,
        branchTo: branchTo.value,
        branchToName: branchTo.options[branchTo.selectedIndex].textContent,
        departmentTo: deptTo.value,
        departmentToName: deptTo.options[deptTo.selectedIndex].textContent,
        comments: document.getElementById('comments').value,
    }

    postTextApi(transferPostOrPutUrl, transfer)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                const branchOptions = branchTo.options
                const selectedBranch = branchOptions[branchOptions.selectedIndex]
                document.getElementById('branch').value = branchTo.value
                document.getElementById('branchName').value = selectedBranch.textContent

                const deptOptions = deptTo.options
                const selectedDept = deptOptions[deptOptions.selectedIndex]
                setTimeout(loadDepartment, 100, selectedDept.value)
                document.getElementById('departmentName').value = selectedDept.textContent

                loadTransferList()
                setTimeout(addTransferRow, 100)
            }
        })
}

function putTransfer(index, isLast) {
    const branchFrom = document.getElementById(`branchFrom_${index}`)
    const branchTo = document.getElementById(`branchTo_${index}`)
    const deptFrom = document.getElementById(`departmentFrom_${index}`)
    const deptTo = document.getElementById(`departmentTo_${index}`)
    const transfer = {
        id: document.getElementById(`transferId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        transferDate: document.getElementById(`transferDate_${index}`).value,
        branchFrom: branchFrom.value,
        branchFromName: branchFrom.options[branchFrom.selectedIndex].textContent,
        departmentFrom: deptFrom.value,
        departmentFromName: deptFrom.options[deptFrom.selectedIndex].textContent,
        branchTo: branchTo.value,
        branchToName: branchTo.options[branchTo.selectedIndex].textContent,
        departmentTo: deptTo.value,
        departmentToName: deptTo.options[deptTo.selectedIndex].textContent,
        comments: document.getElementById(`transferComments_${index}`).value,
    }

    postTextApi(transferPostOrPutUrl, transfer)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                if (isLast && branchTo !== null && deptTo !== null) {
                    const branchOptions = branchTo.options
                    const selectedBranch = branchOptions[branchOptions.selectedIndex]
                    document.getElementById('branch').value = branchTo.value
                    document.getElementById('branchName').value = selectedBranch.textContent

                    const deptOptions = deptTo.options
                    const selectedDept = deptOptions[deptOptions.selectedIndex]
                    setTimeout(loadDepartment, 100, selectedDept.value)
                    document.getElementById('departmentName').value = selectedDept.textContent
                }
                loadTransferList()
            }
        })
}

function deleteTransfer(index, isLast) {
    const branchTo = document.getElementById(`branchTo_${index - 1}`)
    const deptTo = document.getElementById(`departmentTo_${index - 1}`)

    const transferId = document.getElementById(`transferId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(transferDeleteUrl + '?id=' + transferId)
        if (isLast && branchTo !== null && deptTo !== null) {
            const branchOptions = branchTo.options
            const selectedBranch = branchOptions[branchOptions.selectedIndex]
            document.getElementById('branch').value = branchTo.value
            document.getElementById('branchName').value = selectedBranch.textContent

            const deptOptions = deptTo.options
            const selectedDept = deptOptions[deptOptions.selectedIndex]
            setTimeout(loadDepartment, 100, selectedDept.value)
            document.getElementById('departmentName').value = selectedDept.textContent
        }
        setTimeout(loadTransferList, 100)
    }
}

function editTransfer(index) {
    const view = document.querySelectorAll(`.transfer-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.transfer-update-${index}`)
    const transferId = document.getElementById(`transferId_${index}`).value
    const url = `/get-transfer-info?id=${transferId}`

    fetch(url).then(response => response.json()).then(transferInfo => {
        updateView.forEach(el => {
            el.classList.remove('d-none')
            el.classList.add('d-block')
            if (el.id === `transferDate_${index}`) {
                let formattedDate
                if (el.value.includes('-')) {
                    const dateArr = el.value.split('-')
                    formattedDate = dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0]
                } else {
                    formattedDate = el.value
                }
                applyDatepicker(el, formattedDate)
            }
            if (el.id === `branchFrom_${index}`) {
                document.getElementById(`branchFrom_${index}`).value = transferInfo.branchFrom
            }
            if (el.id === `departmentFrom_${index}`) {
                loadTransferDepartment(index, transferInfo.departmentFrom)
            }
            if (el.id === `branchTo_${index}`) {
                document.getElementById(`branchTo_${index}`).value = transferInfo.branchTo
            }
            if (el.id === `departmentTo_${index}`) {
                loadTransferDepartment(index, transferInfo.departmentTo, 'to')
            }
            if (el.id === `transferComments_${index}`) {
                document.getElementById(`transferComments_${index}`).value = transferInfo.comments
            }
        })
    })
}

function restoreTransfer(index) {
    const view = document.querySelectorAll(`.transfer-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.transfer-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addTransferRow() {
    let options = ''
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    const manageBtn = document.querySelector('.manage-transfer-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'transferDynamicRow')
    const row = `
                    <td>
                        <input type="text" placeholder="DD/MM/YYYY" id="transferDate" class="form-control">
                    </td>
                    <td>
                        <select id="branchFrom" class="form-select branch-from-99" onchange="loadTransferDepartment(99, 0)">
                            <option value="0">Please Select</option>
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="departmentFrom" class="form-select department-from-99">
                            ${deptOptions}
                        </select>
                    </td>
                    <td>
                        <select id="branchTo" class="form-select branch-to-99" onchange="loadTransferDepartment(99, 0, 'to')">
                            <option value="0">Please Select</option>
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="departmentTo" class="form-select department-to-99">
                            ${deptOptions}
                        </select>
                    </td>
                    <td><input id="comments" class="form-control" ></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#transferAccordion" onclick="postTransfer()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#transferAccordion" onclick="removeTransferRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#transferTable tbody')
    body.appendChild(rowEl)
    const dateFrom = document.querySelector('#transferDate')
    setTimeout(applyDatepicker, 100, dateFrom, dateFrom.value)
}

function removeTransferRow() {
    document.getElementById('transferDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-transfer-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadTransferList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-transfer-info?employeeId=' + empId
    const tableData = document.querySelector('#transferTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    let options = `<option value="0">-Please Select-</option>`
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    fetch(url).then(response => response.json()).then(data => {
        const rowSize = data.length
        data.forEach((transfer, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.transferDate}
                        </label>
                        <input type="hidden" id="transferId_${index}" value="${transfer.id}">
                        <input type="text" id="transferDate_${index}" class="form-control transfer-update-${index} d-none" value="${transfer.transferDate}">
                    </td>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.branchFromName}
                        </label>
                        <select id="branchFrom_${index}" class="form-select branch-from branch-from-${index} transfer-update-${index} d-none" onchange="loadTransferDepartment(${index}, ${transfer.departmentFrom})">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.departmentFromName}
                        </label>
                        <select id="departmentFrom_${index}" class="form-select department-from department-from-${index} transfer-update-${index} d-none">
                            ${deptOptions}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.branchToName}
                        </label>
                        <select id="branchTo_${index}" class="form-select branch-to branch-to-${index} transfer-update-${index} d-none" onchange="loadTransferDepartment(${index}, ${transfer.departmentTo}, 'to')">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.departmentToName}
                        </label>
                        <select id="departmentTo_${index}" class="form-select department-to department-to-${index} transfer-update-${index} d-none">
                            ${deptOptions}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext transfer-view-${index} d-block">
                            ${transfer.comments || ''}
                        </label>
                        <input type="text" id="transferComments_${index}" class="form-control transfer-update-${index} d-none" value="${transfer.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary transfer-view-${index} d-inline-block d-block" href="#transferAccordion" onclick="editTransfer(${index});loadTransferDepartment(${index}, ${transfer.departmentFrom});loadTransferDepartment(${index}, ${transfer.departmentTo}, 'to')">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary transfer-update-${index} d-inline-block d-none" href="#transferAccordion" onclick="putTransfer(${index}, ${rowSize === (index + 1)})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary transfer-view-${index} d-inline-block d-block" href="#transferAccordion" onclick="deleteTransfer(${index}, ${rowSize === (index + 1)})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary transfer-update-${index} d-inline-block d-none" href="#transferAccordion" onclick="restoreTransfer(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postPreJoin() {
    const branchFrom = document.getElementById('preJoinBranch')
    const deptFrom = document.getElementById('preJoinDept')
    const preJoin = {
        employee: {id: document.getElementById('rowId').value},
        branch: branchFrom.value,
        branchName: branchFrom.options[branchFrom.selectedIndex].textContent,
        department: deptFrom.value,
        departmentName: deptFrom.options[deptFrom.selectedIndex].textContent,
        description: document.getElementById('preJoinDescription').value,
    }

    postTextApi(preJoinPostOrPutUrl, preJoin)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadPreJoinList()
                setTimeout(addPreJoinRow, 100)
            }
        })
}

function putPreJoin(index) {
    const branchFrom = document.getElementById(`branch_${index}`)
    const deptFrom = document.getElementById(`department_${index}`)
    const preJoin = {
        id: document.getElementById(`preJoinId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        branch: branchFrom.value,
        branchName: branchFrom.options[branchFrom.selectedIndex].textContent,
        department: deptFrom.value,
        departmentName: deptFrom.options[deptFrom.selectedIndex].textContent,
        description: document.getElementById(`description_${index}`).value,
    }

    postTextApi(preJoinPostOrPutUrl, preJoin)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadPreJoinList()
            }
        })
}

function deletePreJoin(index) {
    const preJoinId = document.getElementById(`preJoinId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(preJoinDeleteUrl + '?id=' + preJoinId)
        setTimeout(loadPreJoinList, 100)
    }
}

function editPreJoin(index) {
    const view = document.querySelectorAll(`.preJoin-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.preJoin-update-${index}`)
    const preJoinId = document.getElementById(`preJoinId_${index}`).value
    const url = `/get-pre-joining-info?id=${preJoinId}`

    fetch(url).then(response => response.json()).then(preJoinInfo => {
        updateView.forEach(el => {
            el.classList.remove('d-none')
            el.classList.add('d-block')

            if (el.id === `branch_${index}`) {
                document.getElementById(`branch_${index}`).value = preJoinInfo.branch
            }
            if (el.id === `department_${index}`) {
                loadPreJoinDepartment(index,preJoinInfo.department)
            }
            if (el.id === `description_${index}`) {
                document.getElementById(`description_${index}`).value = preJoinInfo.description
            }
        })
    })
}

function restorePreJoin(index) {
    const view = document.querySelectorAll(`.preJoin-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.preJoin-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addPreJoinRow() {
    let options = `<option value="0">-Please Select-</option>`
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    const manageBtn = document.querySelector('.manage-pre-join-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'preJoinDynamicRow')
    const row = `
                    <td>
                        <select id="preJoinBranch" class="form-select preJoin-branch-99" onchange="loadPreJoinDepartment(99, this.value)">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="preJoinDept" class="form-select preJoin-dept-99">
                            ${deptOptions}
                        </select>
                    </td>
                    <td><input id="preJoinDescription" class="form-control" ></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#attachedAccordion" onclick="postPreJoin()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#attachedAccordion" onclick="removePreJoinRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#preJoinTable tbody')
    body.appendChild(rowEl)
}

function removePreJoinRow() {
    document.getElementById('preJoinDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-pre-join-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadPreJoinList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-pre-joining-info?employeeId=' + empId
    const tableData = document.querySelector('#preJoinTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    let options = `<option value="0">-Please Select-</option>`
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((preJoin, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext preJoin-view-${index} d-block">
                            ${preJoin.branchName}
                        </label>
                        <input type="hidden" id="preJoinId_${index}" value="${preJoin.id}">
                        <select id="branch_${index}" class="form-select preJoin-branch-${index} preJoin-update-${index} d-none" onchange="loadPreJoinDepartment(${index}, ${preJoin.branch})">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext preJoin-view-${index} d-block">
                            ${preJoin.departmentName}
                        </label>
                        <select id="department_${index}" class="form-select preJoin-dept-${index} preJoin-update-${index} d-none">
                            ${deptOptions}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext preJoin-view-${index} d-block">
                            ${preJoin.description || ''}
                        </label>
                        <textarea rows="1" id="description_${index}" class="form-control preJoin-update-${index} d-none">
                            ${preJoin.description || ''}
                        </textarea>
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary preJoin-view-${index} d-inline-block d-block" href="#attachedAccordion" onclick="editPreJoin(${index});loadPreJoinDepartment(${index}, ${preJoin.branch})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary preJoin-update-${index} d-inline-block d-none" href="#attachedAccordion" onclick="putPreJoin(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary preJoin-view-${index} d-inline-block d-block" href="#attachedAccordion" onclick="deletePreJoin(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary preJoin-update-${index} d-inline-block d-none" href="#attachedAccordion" onclick="restorePreJoin(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postKhedmotaion() {
    const branchFrom = document.getElementById('khedmotBranch')
    const deptFrom = document.getElementById('khedmotDepartment')
    const khedmotaion = {
        employee: {id: document.getElementById('rowId').value},
        branch: branchFrom.value,
        branchName: branchFrom.options[branchFrom.selectedIndex].textContent,
        department: deptFrom.value,
        departmentName: deptFrom.options[deptFrom.selectedIndex].textContent,
        description: document.getElementById('khedmotDescription').value,
    }

    postTextApi(khedmotaionPostOrPutUrl, khedmotaion)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadKhedmotaionList()
                setTimeout(addKhedmotaionRow, 100)
            }
        })
}

function putKhedmotaion(index) {
    const branchFrom = document.getElementById(`branch_${index}`)
    const deptFrom = document.getElementById(`department_${index}`)
    const khedmotaion = {
        id: document.getElementById(`khedmotaionId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        branch: branchFrom.value,
        branchName: branchFrom.options[branchFrom.selectedIndex].textContent,
        department: deptFrom.value,
        departmentName: deptFrom.options[deptFrom.selectedIndex].textContent,
        description: document.getElementById(`description_${index}`).value,
    }

    postTextApi(khedmotaionPostOrPutUrl, khedmotaion)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadKhedmotaionList()
            }
        })
}

function deleteKhedmotaion(index) {
    const khedmotaionId = document.getElementById(`khedmotaionId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(khedmotaionDeleteUrl + '?id=' + khedmotaionId)
        setTimeout(loadKhedmotaionList, 100)
    }
}

function editKhedmotaion(index) {
    const view = document.querySelectorAll(`.khedmotaion-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.khedmotaion-update-${index}`)
    const khedmotaionId = document.getElementById(`khedmotaionId_${index}`).value
    const url = `/get-khedmotaion-info?id=${khedmotaionId}`

    fetch(url).then(response => response.json()).then(khedmotaionInfo => {
        updateView.forEach(el => {
            el.classList.remove('d-none')
            el.classList.add('d-block')

            if (el.id === `branch_${index}`) {
                document.getElementById(`branch_${index}`).value = khedmotaionInfo.branch
            }
            if (el.id === `department_${index}`) {
                loadKhedmotDepartment(index, khedmotaionInfo.department)
            }
            if (el.id === `description_${index}`) {
                document.getElementById(`description_${index}`).value = khedmotaionInfo.description
            }
        })
    })
}

function restoreKhedmotaion(index) {
    const view = document.querySelectorAll(`.khedmotaion-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.khedmotaion-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addKhedmotaionRow() {
    let options = `<option value="0">-Please Select-</option>`
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    const manageBtn = document.querySelector('.manage-khedmotaion-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'khedmotaionDynamicRow')
    const row = `
                    <td>
                        <select id="khedmotBranch" class="form-select khedmot-branch-99" onchange="loadKhedmotDepartment(99, this.value)">
                            ${options}
                        </select>
                    </td>
                    <td>
                        <select id="khedmotDepartment" class="form-select khedmot-dept-99">
                            ${deptOptions}
                        </select>
                    </td>
                    <td><input id="khedmotDescription" class="form-control" ></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#attachedAccordion" onclick="postKhedmotaion()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#attachedAccordion" onclick="removeKhedmotaionRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#khedmotaionTable tbody')
    body.appendChild(rowEl)
}

function removeKhedmotaionRow() {
    document.getElementById('khedmotaionDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-khedmotaion-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadKhedmotaionList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-khedmotaion-info?employeeId=' + empId
    const tableData = document.querySelector('#khedmotaionTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    let options = `<option value="0">-Please Select-</option>`
    branchList.forEach(obj => options += `<option value="${obj.id}">${obj.name}</option>`)

    let deptOptions = `<option value="0"></option>`
    departmentList.forEach(obj => deptOptions += `<option value="${obj.id}">${obj.name}</option>`)

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((khedmotaion, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext khedmotaion-view-${index} d-block">${khedmotaion.branchName}</label>
                        <input type="hidden" id="khedmotaionId_${index}" value="${khedmotaion.id}">
                        <select id="branch_${index}" class="form-select khedmotaion-update-${index} khedmot-branch-${index} d-none" onchange="loadKhedmotDepartment(${index}, ${khedmotaion.branch})">
                            <option value="0">-Please Select-</option>
                            ${options}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext khedmotaion-view-${index} d-block">${khedmotaion.departmentName}</label>
                        <select id="department_${index}" class="form-select khedmotaion-update-${index} khedmot-dept-${index} d-none">
                            <option value="0">-Please Select-</option>
                            ${deptOptions}
                        </select>
                    </td>
                    <td>
                        <label class="form-control-plaintext khedmotaion-view-${index} d-block">${khedmotaion.description}</label>
                        <textarea rows="1" id="description_${index}" class="form-control khedmotaion-update-${index} d-none">${khedmotaion.description}</textarea>
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary khedmotaion-view-${index} d-inline-block d-block" href="#khedmotaionAccordion" onclick="editKhedmotaion(${index});loadKhedmotDepartment(${index},${khedmotaion.branch})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary khedmotaion-update-${index} d-inline-block d-none" href="#khedmotaionAccordion" onclick="putKhedmotaion(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary khedmotaion-view-${index} d-inline-block d-block" href="#khedmotaionAccordion" onclick="deleteKhedmotaion(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary khedmotaion-update-${index} d-inline-block d-none" href="#khedmotaionAccordion" onclick="restoreKhedmotaion(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `);
        })
    })
}

function postEducation() {
    const education = {
        employee: {id: document.getElementById('rowId').value},
        examName: document.getElementById('examName').value,
        passingYear: document.getElementById('passingYear').value,
        result: document.getElementById('result').value,
        institution: document.getElementById('institution').value,
        board: document.getElementById('board').value,
        subjectDepartment: document.getElementById('subjectDepartment').value,
        comments: document.getElementById('comments').value,
    }

    postTextApi(educationPostOrPutUrl, education)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadEducationList()
                setTimeout(addEducationRow, 100)
            }
        })
}

function putEducation(index) {
    const education = {
        id: document.getElementById(`educationId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        examName: document.getElementById(`examName_${index}`).value,
        passingYear: document.getElementById(`passingYear_${index}`).value,
        result: document.getElementById(`result_${index}`).value,
        institution: document.getElementById(`institution_${index}`).value,
        board: document.getElementById(`board_${index}`).value,
        subjectDepartment: document.getElementById(`subjectDepartment_${index}`).value,
        comments: document.getElementById(`eduComments_${index}`).value,
    }

    postTextApi(educationPostOrPutUrl, education)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadEducationList()
            }
        })
}

function deleteEducation(index) {
    const educationId = document.getElementById(`educationId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(educationDeleteUrl + '?id=' + educationId)
        setTimeout(loadEducationList, 100)
    }
}

function editEducation(index) {
    const view = document.querySelectorAll(`.education-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.education-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })
}

function restoreEducation(index) {
    const view = document.querySelectorAll(`.education-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.education-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addEducationRow() {
    const manageBtn = document.querySelector('.manage-education-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'educationDynamicRow')
    const row = `
                    <td><input type="text" id="examName" class="form-control"></td>
                    <td><input type="text" id="passingYear" class="form-control"></td>
                    <td><input type="text" id="result" class="form-control"></td>
                    <td><input type="text" id="institution" class="form-control"></td>
                    <td><input type="text" id="board" class="form-control"></td>
                    <td><input type="text" id="subjectDepartment" class="form-control"></td>
                    <td><input type="text" id="comments" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#academicAccordion" onclick="postEducation()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#academicAccordion" onclick="removeEducationRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#educationTable tbody')
    body.appendChild(rowEl)
}

function removeEducationRow() {
    document.getElementById('educationDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-education-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadEducationList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-educational-info?employeeId=' + empId
    const tableData = document.querySelector('#educationTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((education, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.examName || ''}</label>
                        <input type="hidden" id="educationId_${index}" value="${education.id}">
                        <input type="text" id="examName_${index}" class="form-control education-update-${index} d-none" value="${education.examName || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.passingYear || ''}</label>
                        <input type="text" id="passingYear_${index}" class="form-control education-update-${index} d-none" value="${education.passingYear || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.result || ''}</label>
                        <input type="text" id="result_${index}" class="form-control education-update-${index} d-none" value="${education.result || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.institution || ''}</label>
                        <input type="text" id="institution_${index}" class="form-control education-update-${index} d-none" value="${education.institution || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.board || ''}</label>
                        <input type="text" id="board_${index}" class="form-control education-update-${index} d-none" value="${education.board || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.subjectDepartment || ''}</label>
                        <input type="text" id="subjectDepartment_${index}" class="form-control education-update-${index} d-none" value="${education.subjectDepartment || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext education-view-${index} d-block">${education.comments || ''}</label>
                        <input type="text" id="eduComments_${index}" class="form-control education-update-${index} d-none" value="${education.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary education-view-${index} d-inline-block d-block" href="#academicAccordion" onclick="editEducation(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary education-update-${index} d-inline-block d-none" href="#academicAccordion" onclick="putEducation(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary education-view-${index} d-inline-block d-block" href="#academicAccordion" onclick="deleteEducation(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary education-update-${index} d-inline-block d-none" href="#academicAccordion" onclick="restoreEducation(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postExperience() {
    const experience = {
        employee: {id: document.getElementById('rowId').value},
        designation: document.getElementById('expDesignation').value,
        institution: document.getElementById('institution').value,
        duration: document.getElementById('duration').value,
        responsibility: document.getElementById('expResponsibility').value,
        comments: document.getElementById('expComments').value,
    }

    postTextApi(experiencePostOrPutUrl, experience)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadExperienceList()
                setTimeout(addExperienceRow, 100)
            }
        })
}

function putExperience(index) {
    const experience = {
        id: document.getElementById(`experienceId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        designation: document.getElementById(`designation_${index}`).value,
        institution: document.getElementById(`expInstitution_${index}`).value,
        duration: document.getElementById(`duration_${index}`).value,
        responsibility: document.getElementById(`responsibility_${index}`).value,
        comments: document.getElementById(`expComments_${index}`).value,
    }

    postTextApi(experiencePostOrPutUrl, experience)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadExperienceList()
            }
        })
}

function deleteExperience(index) {
    const experienceId = document.getElementById(`experienceId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(experienceDeleteUrl + '?id=' + experienceId)
        setTimeout(loadExperienceList, 100)
    }
}

function editExperience(index) {
    const view = document.querySelectorAll(`.experience-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.experience-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })
}

function restoreExperience(index) {
    const view = document.querySelectorAll(`.experience-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.experience-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addExperienceRow() {
    const manageBtn = document.querySelector('.manage-experience-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'experienceDynamicRow')
    const row = `
                    <td><input type="text" id="expDesignation" class="form-control"></td>
                    <td><input type="text" id="institution" class="form-control"></td>
                    <td><input type="text" id="duration" class="form-control"></td>
                    <td><input type="text" id="expResponsibility" class="form-control"></td>
                    <td><input type="text" id="expComments" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#experienceAccordion" onclick="postExperience()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#experienceAccordion" onclick="removeExperienceRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#experienceTable tbody')
    body.appendChild(rowEl)
}

function removeExperienceRow() {
    document.getElementById('experienceDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-experience-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadExperienceList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-experience-info?employeeId=' + empId
    const tableData = document.querySelector('#experienceTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((experience, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext experience-view-${index} d-block">${experience.designation || ''}</label>
                        <input type="hidden" id="experienceId_${index}" value="${experience.id}">
                        <input type="text" id="designation_${index}" class="form-control experience-update-${index} d-none" value="${experience.designation || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext experience-view-${index} d-block">${experience.institution || ''}</label>
                        <input type="text" id="expInstitution_${index}" class="form-control experience-update-${index} d-none" value="${experience.institution || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext experience-view-${index} d-block">${experience.duration || ''}</label>
                        <input type="text" id="duration_${index}" class="form-control experience-update-${index} d-none" value="${experience.duration || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext experience-view-${index} d-block">${experience.responsibility || ''}</label>
                        <input type="text" id="responsibility_${index}" class="form-control experience-update-${index} d-none" value="${experience.responsibility || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext experience-view-${index} d-block">${experience.comments || ''}</label>
                        <input type="text" id="expComments_${index}" class="form-control experience-update-${index} d-none" value="${experience.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary experience-view-${index} d-inline-block d-block" href="#experienceAccordion" onclick="editExperience(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary experience-update-${index} d-inline-block d-none" href="#experienceAccordion" onclick="putExperience(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary experience-view-${index} d-inline-block d-block" href="#experienceAccordion" onclick="deleteExperience(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary experience-update-${index} d-inline-block d-none" href="#experienceAccordion" onclick="restoreExperience(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postTraining() {
    const training = {
        employee: {id: document.getElementById('rowId').value},
        courseName: document.getElementById('courseName').value,
        contents: document.getElementById('contents').value,
        duration: document.getElementById('trainingDuration').value,
        completedYear: document.getElementById('completedYear').value,
        certification: document.getElementById('certification').value,
        comments: document.getElementById('trainingComments').value,
    }

    postTextApi(trainingPostOrPutUrl, training)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadTrainingList()
                setTimeout(addTrainingRow, 100)
            }
        })
}

function putTraining(index) {
    const training = {
        id: document.getElementById(`trainingId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        courseName: document.getElementById(`courseName_${index}`).value,
        contents: document.getElementById(`contents_${index}`).value,
        duration: document.getElementById(`trainingDuration_${index}`).value,
        completedYear: document.getElementById(`completedYear_${index}`).value,
        certification: document.getElementById(`certification_${index}`).value,
        comments: document.getElementById(`trainingComments_${index}`).value,
    }

    postTextApi(trainingPostOrPutUrl, training)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadTrainingList()
            }
        })
}

function deleteTraining(index) {
    const trainingId = document.getElementById(`trainingId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(trainingDeleteUrl + '?id=' + trainingId)
        setTimeout(loadTrainingList, 100)
    }
}

function editTraining(index) {
    const view = document.querySelectorAll(`.training-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.training-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })
}

function restoreTraining(index) {
    const view = document.querySelectorAll(`.training-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.training-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addTrainingRow() {
    const manageBtn = document.querySelector('.manage-training-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'trainingDynamicRow')
    const row = `
                    <td><input type="text" id="courseName" class="form-control"></td>
                    <td><input type="text" id="contents" class="form-control"></td>
                    <td><input type="text" id="trainingDuration" class="form-control"></td>
                    <td><input type="text" id="completedYear" class="form-control"></td>
                    <td><input type="text" id="certification" class="form-control"></td>
                    <td><input type="text" id="trainingComments" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#trainingAccordion" onclick="postTraining()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#trainingAccordion" onclick="removeTrainingRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#trainingTable tbody')
    body.appendChild(rowEl)
}

function removeTrainingRow() {
    document.getElementById('trainingDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-training-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadTrainingList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-training-info?employeeId=' + empId
    const tableData = document.querySelector('#trainingTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((training, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext training-view-${index} d-block">${training.courseName || ''}</label>
                        <input type="hidden" id="trainingId_${index}" value="${training.id}">
                        <input type="text" id="courseName_${index}" class="form-control training-update-${index} d-none" value="${training.courseName || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext text-break training-view-${index} d-block">${training.contents || ''}</label>
                        <input type="text" id="contents_${index}" class="form-control text-break training-update-${index} d-none" value="${training.contents || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext training-view-${index} d-block">${training.duration || ''}</label>
                        <input type="text" id="trainingDuration_${index}" class="form-control training-update-${index} d-none" value="${training.duration || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext training-view-${index} d-block">${training.completedYear || ''}</label>
                        <input type="text" id="completedYear_${index}" class="form-control training-update-${index} d-none" value="${training.completedYear || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext training-view-${index} d-block">${training.certification || ''}</label>
                        <input type="text" id="certification_${index}" class="form-control training-update-${index} d-none" value="${training.certification || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext training-view-${index} d-block">${training.comments || ''}</label>
                        <input type="text" id="trainingComments_${index}" class="form-control training-update-${index} d-none" value="${training.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary training-view-${index} d-inline-block d-block" href="#trainingAccordion" onclick="editTraining(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary training-update-${index} d-inline-block d-none" href="#trainingAccordion" onclick="putTraining(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary training-view-${index} d-inline-block d-block" href="#trainingAccordion" onclick="deleteTraining(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary training-update-${index} d-inline-block d-none" href="#trainingAccordion" onclick="restoreTraining(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postExtraQualification() {
    const extraQualification = {
        employee: {id: document.getElementById('rowId').value},
        description: document.getElementById('eqDescription').value,
    }

    postTextApi(extraQualificationPostOrPutUrl, extraQualification)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadExtraQualificationList()
                setTimeout(addExtraQualificationRow, 100)
            }
        })
}

function putExtraQualification(index) {
    const extraQualification = {
        id: document.getElementById(`extraQualificationId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        description: document.getElementById(`eqDescription_${index}`).value,
    }

    postTextApi(extraQualificationPostOrPutUrl, extraQualification)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadExtraQualificationList()
            }
        })
}

function deleteExtraQualification(index) {
    const extraQualificationId = document.getElementById(`extraQualificationId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(extraQualificationDeleteUrl + '?id=' + extraQualificationId)
        setTimeout(loadExtraQualificationList, 100)
    }
}

function editExtraQualification(index) {
    const view = document.querySelectorAll(`.extraQualification-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.extraQualification-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })
}

function restoreExtraQualification(index) {
    const view = document.querySelectorAll(`.extraQualification-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.extraQualification-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addExtraQualificationRow() {
    const manageBtn = document.querySelector('.manage-qualification-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'extraQualificationDynamicRow')
    const row = `
                    <td><input type="text" id="eqDescription" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#extraQualificationAccordion" onclick="postExtraQualification()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#extraQualificationAccordion" onclick="removeExtraQualificationRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#extraQualificationTable tbody')
    body.appendChild(rowEl)
}

function removeExtraQualificationRow() {
    document.getElementById('extraQualificationDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-qualification-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadExtraQualificationList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-extra-qualification-info?employeeId=' + empId
    const tableData = document.querySelector('#extraQualificationTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((extraQualification, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext extraQualification-view-${index} d-block">${extraQualification.description || ''}</label>
                        <input type="hidden" id="extraQualificationId_${index}" value="${extraQualification.id}">
                        <input type="text" id="eqDescription_${index}" class="form-control extraQualification-update-${index} d-none" value="${extraQualification.description || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary extraQualification-view-${index} d-inline-block d-block" href="#extraQualificationAccordion" onclick="editExtraQualification(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary extraQualification-update-${index} d-inline-block d-none" href="#extraQualificationAccordion" onclick="putExtraQualification(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary extraQualification-view-${index} d-inline-block d-block" href="#extraQualificationAccordion" onclick="deleteExtraQualification(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary extraQualification-update-${index} d-inline-block d-none" href="#extraQualificationAccordion" onclick="restoreExtraQualification(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postLanguage() {
    const language = {
        employee: {id: document.getElementById('rowId').value},
        languageName: document.getElementById('languageName').value,
        description: document.getElementById('langDescription').value,
    }

    postTextApi(languageSkillPostOrPutUrl, language)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadLanguageList()
                setTimeout(addLanguageRow, 100)
            }
        })
}

function putLanguage(index) {
    const language = {
        id: document.getElementById(`languageId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        languageName: document.getElementById(`languageName_${index}`).value,
        description: document.getElementById(`langDescription_${index}`).value,
    }

    postTextApi(languageSkillPostOrPutUrl, language)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadLanguageList()
            }
        })
}

function deleteLanguage(index) {
    const languageId = document.getElementById(`languageId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(languageSkillDeleteUrl + '?id=' + languageId)
        setTimeout(loadLanguageList, 100)
    }
}

function editLanguage(index) {
    const view = document.querySelectorAll(`.language-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.language-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })
}

function restoreLanguage(index) {
    const view = document.querySelectorAll(`.language-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.language-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addLanguageRow() {
    const manageBtn = document.querySelector('.manage-language-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'languageDynamicRow')
    const row = `
                    <td><input type="text" id="languageName" class="form-control"></td>
                    <td><input type="text" id="langDescription" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#languageAccordion" onclick="postLanguage()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#languageAccordion" onclick="removeLanguageRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#languageTable tbody')
    body.appendChild(rowEl)
}

function removeLanguageRow() {
    document.getElementById('languageDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-language-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadLanguageList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-language-skill-info?employeeId=' + empId
    const tableData = document.querySelector('#languageTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((language, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext language-view-${index} d-block">${language.languageName || ''}</label>
                        <input type="hidden" id="languageId_${index}" value="${language.id}">
                        <input type="text" id="languageName_${index}" class="form-control language-update-${index} d-none" value="${language.languageName || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext language-view-${index} d-block">${language.description || ''}</label>
                        <input type="text" id="langDescription_${index}" class="form-control language-update-${index} d-none" value="${language.description || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary language-view-${index} d-inline-block d-block" href="#languageAccordion" onclick="editLanguage(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary language-update-${index} d-inline-block d-none" href="#languageAccordion" onclick="putLanguage(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary language-view-${index} d-inline-block d-block" href="#languageAccordion" onclick="deleteLanguage(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary language-update-${index} d-inline-block d-none" href="#languageAccordion" onclick="restoreLanguage(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postFamily() {
    const family = {
        employee: {id: document.getElementById('rowId').value},
        name: document.getElementById('fName').value,
        relation: document.getElementById('fRelation').value,
        birthDate: document.getElementById('fBirthDate').value,
        phone: document.getElementById('fPhone').value,
        comments: document.getElementById('fComments').value,
    }

    postTextApi(familyPostOrPutUrl, family)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadFamilyList()
                setTimeout(addFamilyRow, 100)
            }
        })
}

function putFamily(index) {
    const family = {
        id: document.getElementById(`familyId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        name: document.getElementById(`fName_${index}`).value,
        relation: document.getElementById(`fRelation_${index}`).value,
        birthDate: document.getElementById(`fBirthDate_${index}`).value,
        phone: document.getElementById(`fPhone_${index}`).value,
        comments: document.getElementById(`fComments_${index}`).value,
    }

    postTextApi(familyPostOrPutUrl, family)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadFamilyList()
            }
        })
}

function deleteFamily(index) {
    const familyId = document.getElementById(`familyId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(familyDeleteUrl + '?id=' + familyId)
        setTimeout(loadFamilyList, 100)
    }
}

function editFamily(index) {
    const view = document.querySelectorAll(`.family-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.family-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
        if (el.id === `fBirthDate_${index}`) {
            let formattedDate
            if (el.value.includes('-')) {
                const dateArr = el.value.split('-')
                formattedDate = dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0]
            } else {
                formattedDate = el.value
            }
            applyDatepicker(el, formattedDate)
        }
    })
}

function restoreFamily(index) {
    const view = document.querySelectorAll(`.family-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.family-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addFamilyRow() {
    const manageBtn = document.querySelector('.manage-family-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'familyDynamicRow')
    const row = `
                    <td><input type="text" id="fName" class="form-control"></td>
                    <td><input type="text" id="fRelation" class="form-control"></td>
                    <td><input type="text" id="fBirthDate" class="form-control"></td>
                    <td><input type="text" id="fPhone" class="form-control"></td>
                    <td><input type="text" id="fComments" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#familyAccordion" onclick="postFamily()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#familyAccordion" onclick="removeFamilyRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#familyTable tbody')
    body.appendChild(rowEl)
    const birthDate = document.getElementById('fBirthDate')
    applyDatepicker(birthDate, birthDate.value)
}

function removeFamilyRow() {
    document.getElementById('familyDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-family-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadFamilyList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-family-info?employeeId=' + empId
    const tableData = document.querySelector('#familyTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((family, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext family-view-${index} d-block">${family.name || ''}</label>
                        <input type="hidden" id="familyId_${index}" value="${family.id}">
                        <input type="text" id="fName_${index}" class="form-control family-update-${index} d-none" value="${family.name || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext family-view-${index} d-block">${family.relation || ''}</label>
                        <input type="text" id="fRelation_${index}" class="form-control family-update-${index} d-none" value="${family.relation || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext family-view-${index} d-block">${family.birthDate}</label>
                        <input type="text" id="fBirthDate_${index}" class="form-control family-update-${index} d-none" value="${family.birthDate}">
                    </td>
                    <td>
                        <label class="form-control-plaintext family-view-${index} d-block">${family.phone || ''}</label>
                        <input type="text" id="fPhone_${index}" class="form-control family-update-${index} d-none" value="${family.phone || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext family-view-${index} d-block">${family.comments || ''}</label>
                        <input type="text" id="fComments_${index}" class="form-control family-update-${index} d-none" value="${family.comments || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary family-view-${index} d-inline-block d-block" href="#familyAccordion" onclick="editFamily(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary family-update-${index} d-inline-block d-none" href="#familyAccordion" onclick="putFamily(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary family-view-${index} d-inline-block d-block" href="#familyAccordion" onclick="deleteFamily(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary family-update-${index} d-inline-block d-none" href="#familyAccordion" onclick="restoreFamily(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}

function postPunishment() {
    const punishment = {
        employee: {id: document.getElementById('rowId').value},
        showCauseDate: document.getElementById('scDate').value,
        showCauseDescription: document.getElementById('scDescription').value,
        actionDate: document.getElementById('actionDate').value,
        actionDescription: document.getElementById('actionDescription').value,
    }

    postTextApi(punishmentPostOrPutUrl, punishment)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadPunishmentList()
                setTimeout(addPunishmentRow, 100)
            }
        })
}

function putPunishment(index) {
    const punishment = {
        id: document.getElementById(`punishmentId_${index}`).value,
        employee: {id: document.getElementById('rowId').value},
        showCauseDate: document.getElementById(`showCauseDate_${index}`).value,
        showCauseDescription: document.getElementById(`showCauseDescription_${index}`).value,
        actionDate: document.getElementById(`actionDate_${index}`).value,
        actionDescription: document.getElementById(`actionDescription_${index}`).value,
    }

    postTextApi(punishmentPostOrPutUrl, punishment)
        .then(result => JSON.parse(result))
        .then(data => {
            if (data.status === 'Success') {
                loadPunishmentList()
            }
        })
}

function deletePunishment(index) {
    const punishmentId = document.getElementById(`punishmentId_${index}`).value
    const confirmation = confirm('Do you really want to delete?')
    if (confirmation) {
        postTextApi(punishmentDeleteUrl + '?id=' + punishmentId)
        setTimeout(loadPunishmentList, 100)
    }
}

function editPunishment(index) {
    const view = document.querySelectorAll(`.punishment-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })

    const updateView = document.querySelectorAll(`.punishment-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
        if ((el.id === `showCauseDate_${index}` || el.id === `actionDate_${index}`)) {
            let formattedDate
            if (el.value.includes('-')) {
                const dateArr = el.value.split('-')
                formattedDate = dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0]
            } else {
                formattedDate = el.value
            }
            applyDatepicker(el, formattedDate)
        }
    })
}

function restorePunishment(index) {
    const view = document.querySelectorAll(`.punishment-view-${index}`)
    view.forEach(el => {
        el.classList.remove('d-none')
        el.classList.add('d-block')
    })

    const updateView = document.querySelectorAll(`.punishment-update-${index}`)
    updateView.forEach(el => {
        el.classList.remove('d-block')
        el.classList.add('d-none')
    })
}

function addPunishmentRow() {
    const manageBtn = document.querySelector('.manage-punishment-button')
    manageBtn.classList.remove('d-block')
    manageBtn.classList.add('d-none')

    const rowEl = document.createElement('TR')
    rowEl.setAttribute('id', 'punishmentDynamicRow')
    const row = `
                    <td><input type="text" id="scDate" class="form-control"></td>
                    <td><input type="text" id="scDescription" class="form-control"></td>
                    <td><input type="text" id="actionDate" class="form-control"></td>
                    <td><input type="text" id="actionDescription" class="form-control"></td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary" href="#punishmentAccordion" onclick="postPunishment()">
                                <i class="bi bi-save-fill"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary" href="#punishmentAccordion" onclick="removePunishmentRow()">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
            `
    rowEl.innerHTML = row

    const body = document.querySelector('#punishmentTable tbody')
    body.appendChild(rowEl)
    const scDate = document.getElementById('scDate')
    applyDatepicker(scDate, scDate.value)
    const actionDate = document.getElementById('actionDate')
    applyDatepicker(actionDate, actionDate.value)
}

function removePunishmentRow() {
    document.getElementById('punishmentDynamicRow').remove()

    const manageBtn = document.querySelector('.manage-punishment-button')
    manageBtn.classList.remove('d-none')
    manageBtn.classList.add('d-block')
}

function loadPunishmentList() {
    const empId = document.getElementById('rowId').value
    const url = '/employees-punishment-info?employeeId=' + empId
    const tableData = document.querySelector('#punishmentTable')
    const tbody = tableData.querySelector('tbody')
    tbody.innerHTML = ''

    fetch(url).then(response => response.json()).then(data => {
        data.forEach((punishment, index) => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>
                        <label class="form-control-plaintext punishment-view-${index} d-block">${punishment.showCauseDate}</label>
                        <input type="hidden" id="punishmentId_${index}" value="${punishment.id}">
                        <input type="text" id="showCauseDate_${index}" class="form-control punishment-update-${index} d-none" value="${punishment.showCauseDate}">
                    </td>
                    <td>
                        <label class="form-control-plaintext punishment-view-${index} d-block">${punishment.showCauseDescription || ''}</label>
                        <input type="text" id="showCauseDescription_${index}" class="form-control punishment-update-${index} d-none" value="${punishment.showCauseDescription || ''}">
                    </td>
                    <td>
                        <label class="form-control-plaintext punishment-view-${index} d-block">${punishment.actionDate}</label>
                        <input type="text" id="actionDate_${index}" class="form-control punishment-update-${index} d-none" value="${punishment.actionDate}">
                    </td>
                    <td>
                        <label class="form-control-plaintext punishment-view-${index} d-block">${punishment.actionDescription || ''}</label>
                        <input type="text" id="actionDescription_${index}" class="form-control punishment-update-${index} d-none" value="${punishment.actionDescription || ''}">
                    </td>
                    <td>
                        <div class="input-group">
                            <a class="link-primary punishment-view-${index} d-inline-block d-block" href="#punishmentAccordion" onclick="editPunishment(${index})">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <a class="link-primary punishment-update-${index} d-inline-block d-none" href="#punishmentAccordion" onclick="putPunishment(${index})">
                                <i class="bi bi-check2-square"></i>
                            </a>
                            <span>&nbsp;&nbsp; | &nbsp;&nbsp;</span>
                            <a class="link-primary punishment-view-${index} d-inline-block d-block" href="#punishmentAccordion" onclick="deletePunishment(${index})">
                                <i class="bi bi-trash"></i>
                            </a>
                            <a class="link-primary punishment-update-${index} d-inline-block d-none" href="#punishmentAccordion" onclick="restorePunishment(${index})">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `)
        })
    })
}