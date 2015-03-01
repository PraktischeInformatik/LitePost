(function(){
  mobile_hover();
  responsive_tables();
})();

//toggle menu for mobile
var menu;
function toggle_menu() {
  if(menu===undefined) {
    menu = document.getElementById('main-navigation')
  }
  if(menu.className==='show') {
    menu.className = '';
  }else {
    menu.className = 'show';  
  }
}

//fix hovering for mobile
function mobile_hover() {
  if('ontouchstart' in window) {
    var login = document.getElementById('profile');
    var link = login.getElementsByTagName('a')[0];
    link.ontouchstart = function() {
      if(login.className === '') {
        login.className = 'hover';
      } else {
        login.className = '';
      }
      return false;
    };
  }
}

//add data-headers to tables
function responsive_tables() {
  var tables = document.getElementsByTagName("table");
  for (var i = tables.length - 1; i >= 0; i--) {
    if(tables[i].className.indexOf("no-collapse") === -1) {
      add_data_headers(tables[i]);
    }
  };
}

function add_data_headers(table) {
  var headers = table.querySelectorAll("thead > tr > th");
  var rows = table.querySelectorAll("tbody > tr");
  for (var i = rows.length - 1; i >= 0; i--) {
    var cells = rows[i].getElementsByTagName("td");
    var length = Math.min(cells.length, headers.length);
    for (var j = length - 1; j >= 0; j--) {
      cells[j].setAttribute("data-header", headers[j].innerHTML);
    };
  };
}