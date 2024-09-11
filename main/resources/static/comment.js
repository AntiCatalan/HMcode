function comment(wendu, shidu, qiwei, guangqiang) {
    var retM="";
    if(wendu >= 35) retM = retM + "温度过高，建议进行降温操作<br>";
    if(shidu <= 25) retM = retM + "湿度过低，建议进行洒水操作<br>";
    if(qiwei >= 600) retM = retM + "空气中有害气体过多，建议打开风扇通风换气<br>";
    if(guangqiang >= 600) retM = retM + "光强过大，建议打开遮光装置<br>";
    if(guangqiang <= 200) retM = retM + "光强过低，建议打开补光装置<br>";
    return retM;
}