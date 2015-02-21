var picker = new Pikaday({
  field: document.querySelectorAll("#date-div input")[0]
});

function toggle_event(checkbox) {
  var input = document.getElementById("date-div");
  if(checkbox.checked) {
    input.className = "show"
  }else {
    input.className = ""
  }
}

function serialize_text() {
  var content = document.getElementById("input-content");
  content.value = editor.serialize()["input-editor"].value;
}

var editor = new MediumEditor('#input-editor', {
   placeholder: 'Inhalt',
   buttons: ['bold', 'italic', 'underline', 'anchor', 'header2']
});