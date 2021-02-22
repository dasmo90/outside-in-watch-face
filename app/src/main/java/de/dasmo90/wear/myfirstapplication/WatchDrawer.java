package de.dasmo90.wear.myfirstapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

public class WatchDrawer {

  private final Canvas canvas;
  private final Paint paint = new Paint();

  public WatchDrawer(Canvas canvas) {
    this.canvas = canvas;
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    paint.setAntiAlias(true);
  }

  public void drawTicks(float radius) {
    for(double i = 0;i < 12;i++) {
      double angle = Math.PI * (i / ((double) 12 / 2) - 0.5);
      PointF start = pointF(radius, angle, 0.5F);
      PointF end = pointF(radius, angle, 0.3F);
      canvas.drawLine(start.x, start.y, end.x, end.y, paint);
    }
  }

  public void drawPointer(int value, int maxValue, float radius, float length, float thickness) {

    double angle = Math.PI * (value / ((double) maxValue / 2) - 0.5);
    float x = (float) Math.cos(angle) * radius;
    float y = (float) Math.sin(angle) * radius;
    Path path = new Path();
    PointF start = pointF(radius, angle, length);
    path.moveTo(start.x, start.y);

    PointF first = pointF(radius, angle - thickness * Math.PI / maxValue, 0F);
    path.lineTo(first.x, first.y);
    PointF middle = pointF(radius, angle, -1.F);
    path.lineTo(middle.x, middle.y);
    PointF second = pointF(radius, angle + thickness * Math.PI / maxValue, 0F);
    path.lineTo(second.x, second.y);
    path.lineTo(start.x, start.y);
    path.close();


    canvas.drawPath(path, paint);

  }

  private PointF pointF(float radius, double angle, float length) {
    float distance = radius - radius * length;
    float x = (float) Math.cos(angle) * distance;
    float y = (float) Math.sin(angle) * distance;
    return new PointF(radius + x, radius + y);
  }
}
