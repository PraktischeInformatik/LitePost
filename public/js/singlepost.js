var
   imagebox,
   timeout;

(function(){
   imagebox = document.getElementById('image-box');
})();

function enlarge_image(image) {
   clearTimeout(timeout);
   image_= image;
   var rect = image.getClientRects()[0];

   imagebox.style.backgroundImage = 'url("' + image.src + '")';

   imagebox.className = 'show';
   setTimeout(function() {
      imagebox.className = 'show full';
   }, 10);
   
}

function hide_image() {
   clearTimeout(timeout);
   imagebox.className = 'show';
   timeout = setTimeout(function() {
      imagebox.className = '';
   }, 333);
}