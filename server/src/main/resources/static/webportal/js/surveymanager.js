var selectedRowIndex = -1;

function getQuestions() {
    var g = document.getElementById("questiontable");
    var surveyId = sessionStorage.getItem("surveyid");
    g.innerHTML = "";

    $.ajax({
        url: '/api/survey/' + surveyId + '/object',
        type: 'GET',
        beforeSend: function (request) {
            request.setRequestHeader("token", sessionStorage.getItem("token"));
        },
        success: function (response) {
            document.getElementById("nametext").value = response.title;
            document.getElementById("descriptiontext").value = response.description;
            var freqType = response.frequencyType;
            if(freqType === 'ALWAYS') {
                setFrequencySelection(1);
            } else if(freqType === 'ONCE') {
                setFrequencySelection(2);
            } else if(freqType === 'DATE') {
                var date = toDateTime(response.frequencyValue);
                document.getElementById("dateselector").value = date.getFullYear() + '-'
                    + (date.getMonth()+1 < 10? '0' : '') + (date.getMonth()+1) + '-'
                    + (date.getDate() < 10? '0' : '') + (date.getDate());
                setFrequencySelection(4);
            } else {
                setFrequencySelection(3);
                document.getElementById("frequencyval").value = response.frequencyValue;
                if(freqType === 'DAYS') {
                    document.getElementById("selectevery").selectedIndex = 1;
                } else if(freqType === 'WEEKS') {
                    document.getElementById("selectevery").selectedIndex = 2;
                } else if(freqType === 'MONTHS') {
                    document.getElementById("selectevery").selectedIndex = 3;
                } else if(freqType === 'YEARS') {
                    document.getElementById("selectevery").selectedIndex = 4;
                } else if(freqType === 'BIRTHDAY') {
                    document.getElementById("selectevery").selectedIndex = 5;
                }
            }

            response.questions.forEach(function (value) {
                g.innerHTML += "<tr>\n" +
                    "<td>\n" + value.title + " " +
                    " <button id='removequestionbuttom" + value.id + "' type=\"button\" class=\"btn btn-danger btn-sm\" onclick=\"removeQuestion(" + value.id + ")\" style='float: right;'>-</button>\n" +
                    "</td>\n" +
                    "</tr>";
            });
        }
    })
}

function removeQuestion(questionId) {
    if (confirm("Er du sikker på at du vil slette spørgsmålet?")) {
        var surveyId = sessionStorage.getItem("surveyid");

        $.ajax({
            url: '/api/survey/' + surveyId + '/question/' + questionId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader("token", sessionStorage.getItem("token"));
            },
            success: function (response) {
                getQuestions();
            }
        })
    }
}

function pickInputTypeDiv() {
    var dropdown = document.getElementById("questionInputTypeInput");

    document.getElementById("textDiv").style.display = "none";
    document.getElementById("numberDiv").style.display = "none";
    document.getElementById("dropdownDiv").style.display = "none";

    if (dropdown.selectedIndex === 1) {
        document.getElementById("textDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex === 2) {
        document.getElementById("numberDiv").style.display = "block";
    }
    else if (dropdown.selectedIndex === 3) {
        document.getElementById("dropdownDiv").style.display = "block";
    }
}

function dropdownValueClicked(row) {
    var table = document.getElementById("dropdownValuesTable");
    for (i = 0; i < table.rows.length; i++) {
        table.rows[i].classList.remove("table-active");
    }

    selectedRowIndex = row.rowIndex;
    row.classList.add("table-active");
}

function addEmptySurvey() {
    var surveyId;
    $.ajax({
        url: '/api/survey',
        type: 'POST',
        success: function (response) {
            console.log("response " + response);
            sessionStorage.setItem("surveyid", response);
        },
        error: function (response) {
            window.location.replace("../home/index.html");
        }
    });
}

function load() {
    if (sessionStorage.getItem("surveyid") != null && sessionStorage.getItem("surveyid") !== "undefined") {
        getQuestions();
    } else {
        addEmptySurvey();
    }
}

function addQuestion() {
    var name = document.getElementById("questionNameInput").value;
    var description = document.getElementById("questionDescriptionInput").value;
    var index = document.getElementById("questionInputTypeInput").selectedIndex;
    var inputtype;
    var type = "STRING";
    var values = [];

    if (index === 1) {
        inputtype = "TEXT";
    } else if (index === 2) {
        inputtype = "NUMBER";
    } else if (index === 3) {
        inputtype = "DROPDOWN";
        var tbl = document.getElementById("dropdownValuesTable");
        for (var i = 0, row; row = tbl.rows[i]; i++) {
            for (var j = 0, col; col = row.cells[j]; j++) {
                values.push(col.innerHTML);
            }
        }
        //  document.getElementById("dropdownValuesTable").getElementsByTagName("tr").forEach(function (value) {
        //    values.push(value.getElementsByTagName("td")[0].value);
        //    });
    }

    var json = {};
    json.title = name;
    json.description = description;
    json.input = inputtype;
    json.type = type;
    json.values = values;

    console.log(json);

    $.ajax({
        url: '/api/survey/' + sessionStorage.getItem("surveyid") + "/question",
        type: 'POST',
        data: JSON.stringify(json),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        success: function (response) {
            console.log("response" + response);
            getQuestions();
        },
        error: function (error) {
            console.log("error" + JSON.stringify(error));
        }
    });
}

function removeValueFromTable() {
    document.getElementById("dropdownValuesTable").deleteRow(selectedRowIndex);
}

function addValueToTable() {
    $('#dropdownValuesTable').append('<tr onclick="dropdownValueClicked(this)">\n' +
        '    <td>' + document.getElementById("dropdownValueText").value + '</td>\n' +
        '</tr>');
}

function updateSurveyMetadata() {
    var title = document.getElementById("nametext").value;
    var description = document.getElementById("descriptiontext").value;
    var frequencyType, frequencyValue;


    if (document.getElementById("radioalways").checked) {
        frequencyType = "ALWAYS";
        frequencyValue = "1";
    } else if (document.getElementById("radioonce").checked) {
        frequencyType = "ONCE";
        frequencyValue = "1";
    } else if (document.getElementById("radioevery").checked) {
        var select = document.getElementById("selectevery").value;
        console.log("select " + select);

        if (select === "1") {
            frequencyType = "DAYS";
        } else if (select === "2") {
            frequencyType = "WEEKS";
        } else if (select === "3") {
            frequencyType = "MONTHS";
        } else if (select === "4") {
            frequencyType = "YEARS";
        } else if (select === "5") {
            frequencyType = "BIRTHDAY";
        }

        frequencyValue = document.getElementById("frequencyval").value;
        console.log("frequency type " + frequencyType);
    } else if (document.getElementById("radiodate").checked) {
        frequencyType = "DATE";
        frequencyValue = document.getElementById("dateselector").value;
    }

    var json = {};
    json.title = title;
    json.description = description;
    json.frequencyType = frequencyType;
    json.frequencyValue = frequencyValue;
    console.log("json " + JSON.stringify(json));

    $.ajax({
        url: '/api/survey/' + sessionStorage.getItem("surveyid"),
        type: 'PUT',
        data: JSON.stringify(json),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        success: function (response) {
            console.log("response" + response);
            getQuestions();
        },
        error: function (error) {
            console.log("error" + JSON.stringify(error));
        }
    });
}

function setFrequencySelection(selection) {
    if (selection !== 3) {
        document.getElementById("frequencyval").disabled = true;
        document.getElementById("selectevery").disabled = true;
    } else {
        //resetFrequencySelection();
        document.getElementById("frequencyval").disabled = false;
        document.getElementById("selectevery").disabled = false;
        document.getElementById("radioevery").checked = true;
    }

    if (selection !== 4) {
        document.getElementById("dateselector").disabled = true;
    } else {
        //resetFrequencySelection();
        document.getElementById("dateselector").disabled = false;
        document.getElementById("radiodate").checked = true;
    }

    if (selection === 1) {
        document.getElementById("radioalways").checked = true;
    } else if (selection === 2) {
        document.getElementById("radioonce").checked = true;
    }
}

function resetFrequencySelection() {
    document.getElementById("radioalways").checked = false;
    document.getElementById("radioonce").checked = false;
    document.getElementById("radioevery").checked = false;
    document.getElementById("radiodate").checked = false;
}

function toDateTime(secs) {
    var t = new Date(1970, 0, 1);
    t.setSeconds(secs);
    return t;
}