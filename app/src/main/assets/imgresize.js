function ResizeAllImage()
{
    var w = window.screen.width*0.8;
    var imgs = document.getElementsByTagName("img");
    for(var i=0;i<imgs.length;i++){
        var imgNaturalWidth = imgs[i].naturalWidth;
        var imgNaturalHeight = imgs[i].naturalheight;
        imgs[i].height = imgNaturalHeight*imgNaturalWidth/w;
        imgs[i].width = w;
    }
}
