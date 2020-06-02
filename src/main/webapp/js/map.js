function resizePfGmapFullScreen() {
	var width = window.innerWidth -10;
	var height = window.innerHeight -10;
	var element = document.getElementById("form-map:map");
	element.style.width = width + "px";
	element.style.height = height + "px";
}

window.onload = function () {
	window.dispatchEvent(new Event('resize'));
};

window.onresize = function () {
	resizePfGmapFullScreen();
};	