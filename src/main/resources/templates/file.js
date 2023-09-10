document.addEventListener("DOMContentLoaded", function() {
    const addParamButton = document.getElementById("addParamButton");
    addParamButton.addEventListener("click", addParam);

    function addParam() {
        const container = document.getElementById("paramsContainer");
        const paramDiv = document.createElement("div");
        paramDiv.innerHTML = `
            <input type="text" th:field="*{paramsMap[__${index}__]}" placeholder="Key=Value">
            <button type="button" class="removeButton" onclick="removeParam(this)">-</button>
        `;
        container.appendChild(paramDiv);
        index++;
    }

    window.removeParam = function(button) {
        const paramDiv = button.parentNode;
        paramDiv.remove();
    }
});