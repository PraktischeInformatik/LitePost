var
  picker,
  editor,
  pikatime,
  images,
  datetime_format = 'YYYY-MM-DD hh:mm';

(function() {
  moment.locale("de");
  picker = new Pikaday({
    format: 'DD. MMMM YYYY',
    field: document.getElementById("pikaday"),
    onSelect: pikaday_select
  });  

  pikatime = document.getElementById("pikatime");
  pikatime.onchange = pikaday_select;
  set_moment();
  var box = document.querySelector('input[onclick="toggle_event(this)"]')
  toggle_event(box);
  editor = new MediumEditor('#input-editor', {
    placeholder: 'Inhalt',
    buttons: ['bold', 'italic', 'underline', 'anchor', 'header2']
  });

  images = document.getElementById('images')
})();

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

function pikaday_select() {
  var input = document.getElementById("pikaday-field");
  var time = pikatime.value;
  var hour = parseInt(time.substr(0, 2));
  var minute = parseInt(time.substr(2));
  var datetime = picker.getMoment() 
  datetime.hour(hour);
  datetime.minute(minute);
  input.value = datetime.format(datetime_format);
}

function set_moment(datetime) {
  if(datetime !== undefined) {
    picker.setMoment(datetime);
    var minute = (Math.round(datetime.minute()/15) * 15) % 60;
    minute = ("00" + String(minute)).slice(-2);
    var hour = datetime.hour();
    hour = ("00" + String(hour)).slice(-2);
    pikatime.value = hour + minute;
  } else {
    var input = document.getElementById("pikaday-field");
    if(input.value !== "") {
      set_moment(moment(input.value, datetime_format))
    }else {
      set_moment(moment());
    }
  }
}

function enumerate_images() {
  var inputs = images.getElementsByTagName('input');
  if(inputs.length === 0) {
    add_image();
  }
  for (var i = inputs.length - 1; i >= 0; i--) {
    inputs[i].name = 'image' + i;
  }; 
}

function add_image() {
  var label = document.createElement('label');

  var input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';

  var button = document.createElement('button');
  button.setAttribute('onclick', 'remove_image(this.parentElement); return false;');
  button.textContent = 'Entfernen';

  label.appendChild(input);
  label.appendChild(button);

  images.appendChild(label);
  enumerate_images()
}


function remove_image(image) {
  image.parentElement.removeChild(image);
  enumerate_images();
}