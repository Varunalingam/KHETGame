package com.vssve.khet;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class KHETBoard extends View {

    int CurrentPlayer = 1;

    int gridx = 10;
    int gridy = 8;

    private int Thickness, CenterConstant;


    List<PlayerPieces> P1, P2;

    PlayerPieces CurrentPiece;

    int padding;

    int BoardColor,SelectedColor,LaserColor;

    Paint BoardPaint,SelectedPaint,LaserPaint;

    Position MinusR,PlusR;
    ArrayList<ArrayList<Position>> Positions;

    int oblayer = 0;

    private Position Selected;

    ValueAnimator LaserAnim;
    ArrayList<PlayerPieces> Laser;
    Path LaserPath;

    int CaseAction = 0;


    public KHETBoard(Context context, AttributeSet attr) {
        super(context,attr);


        BoardColor = Color.GRAY;
        SelectedColor = Color.YELLOW;
        LaserColor = Color.GREEN;

        padding = 5 *(int) getResources().getDisplayMetrics().density;

        Positions = new ArrayList<>();

        BoardPaint = new Paint();
        BoardPaint.setColor(BoardColor);
        BoardPaint.setStyle(Paint.Style.FILL);

        SelectedPaint = new Paint();
        SelectedPaint.setColor(SelectedColor);
        SelectedPaint.setStyle(Paint.Style.FILL);

        LaserPaint = new Paint();
        LaserPaint.setColor(LaserColor);
        LaserPaint.setStrokeWidth(10);
        LaserPaint.setStrokeJoin(Paint.Join.ROUND);
        LaserPaint.setStyle(Paint.Style.STROKE);

        LaserPath = new Path();

        LoadConfig(1);

    }


    void LoadConfig(int i)
    {
        P1 = new ArrayList<>();
        P2 = new ArrayList<>();

        if (i == 1)
        {
            //Classic
            P1.add(new PlayerPieces(5,0,0,0,1));
            P1.add(new PlayerPieces(4, 0,3,0,1));
            P1.add(new PlayerPieces( 4,0,4,0,1));
            P1.add(new PlayerPieces(4, 2,1,0,1));
            P1.add(new PlayerPieces(4,6,5,0,1));
            P1.add(new PlayerPieces(4,7,0,0,1));
            P1.add(new PlayerPieces(4, 7,3,0,1));
            P1.add(new PlayerPieces(4,7,4,0,1));
            P1.add(new PlayerPieces(2,4,3,0,1));
            P1.add(new PlayerPieces(2,5, 3,0,1));
            P1.add(new PlayerPieces(3, 4,0,0,2));
            P1.add(new PlayerPieces(3,6,0,0,2));
            P1.add(new PlayerPieces(1,5,0,0,1));

            for (int j = 0; j < P1.size();j++)
            {
                PlayerPieces  A = new PlayerPieces(P1.get(j).type,P1.get(j).lx,P1.get(j).ly,P1.get(j).dir,P1.get(j).State);
                A.lx = gridx - 1 - A.lx;
                A.ly = gridy - 1 - A.ly;
                A.rotate(2);
                P2.add(A);
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Thickness = (getWidth() - (11 * padding))/10;
        CenterConstant = (getHeight() - (Thickness* 8) - (9 * padding) )/2;

        if (Positions.size() == 0)
        {
            for (int i = 0; i < gridx; i ++)
            {
                ArrayList<Position> set = new ArrayList<>();
                for (int j  = 0; j < gridy; j++)
                {
                    set.add(new Position(new Rect( padding + i * (Thickness + padding), CenterConstant + padding + j * (Thickness + padding) ,  i * (Thickness + padding) + Thickness, CenterConstant + j * (Thickness + padding) + Thickness),i,j));
                }
                Positions.add(set);
            }

            MinusR = new Position(new Rect( padding + ((gridx-1)/2) * (Thickness + padding), CenterConstant + padding + (gridy + 2) * (Thickness + padding) ,  ((gridx-1)/2) * (Thickness + padding) + Thickness, CenterConstant + (gridy + 2) * (Thickness + padding) + Thickness),(gridx - 1)/2,gridy + 2);
            PlusR = new Position(new Rect( padding + ((gridx+1)/2) * (Thickness + padding), CenterConstant + padding + (gridy + 2) * (Thickness + padding) ,  ((gridx +1)/2) * (Thickness + padding) + Thickness, CenterConstant + (gridy + 2) * (Thickness + padding) + Thickness),(gridx + 1)/2,gridy + 2);
        }

        for (int i = 0; i < gridx;i++)
        {
            for (int j  = 0; j < gridy; j++)
            {
                canvas.drawRect( Positions.get(i).get(j).grid, BoardPaint);
            }
        }

        if (Selected != null)
        {
            if (CurrentPiece.type != 5)
            for (int i = -1; i < 2 ; i++)
            {
                if (i + Selected.lx >= 0 && i + Selected.lx < gridx)
                    for (int j = -1;j < 2; j++)
                    {
                        if (j + Selected.ly >= 0 && j + Selected.ly < gridy && !(j==0 && i == 0))
                        {
                            canvas.drawRect(Positions.get(i+Selected.lx).get(j + Selected.ly).grid,SelectedPaint);
                        }
                    }
            }
        }

        for(int i = 0; i < P1.size();i++)
        {
            Bitmap A = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 1 + "pp" + P1.get(i).type, "drawable",getContext().getPackageName()));
            if (P1.get(i).type == 3 && P1.get(i).State == 2)
            {
                A = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 1 + "pp" + 6, "drawable",getContext().getPackageName()));
            }
            Matrix s = new Matrix();
            s.setRotate(90 * P1.get(i).dir);
            A = Bitmap.createBitmap(A,0,0,A.getWidth(),A.getHeight(),s,true);
            canvas.drawBitmap(A,null, Positions.get(P1.get(i).lx).get(P1.get(i).ly).grid,null);
        }
        for(int i = 0; i < P2.size();i++)
        {
            Bitmap A = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 2 + "pp" + P2.get(i).type, "drawable",getContext().getPackageName()));
            if (P2.get(i).type == 3 && P2.get(i).State == 2)
            {
                A = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 2 + "pp" + 6, "drawable",getContext().getPackageName()));
            }
            Matrix s = new Matrix();
            s.setRotate(90 * P2.get(i).dir);
            A = Bitmap.createBitmap(A,0,0,A.getWidth(),A.getHeight(),s,true);
            canvas.drawBitmap(A,null, Positions.get(P2.get(i).lx).get(P2.get(i).ly).grid,null);
        }

        if (CurrentPiece != null && CurrentPiece.type != 3) {
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.plus), null, PlusR.grid, null);
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.minus), null, MinusR.grid, null);
        }

        canvas.drawPath(LaserPath,LaserPaint);
        if (Selected != null)
        {
            canvas.drawCircle(Selected.grid.centerX(),Selected.grid.centerY(),Thickness/5,SelectedPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Position Local = SearchPositions((int) event.getX(),(int) event.getY());

                if (CurrentPiece == null && Local != null)
                {
                    PlayerPieces Cp = isPiece(Local);
                    if (Cp != null)
                    {
                        Selected = Local;
                        CurrentPiece = Cp;
                        if (Cp.type == 3)
                        {
                            Toast.makeText(getContext(),"Move as " + (oblayer == 1 ? "Unstacked" : "Stacked")+ " Piece",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Selected = null;
                        oblayer = 0;
                    }
                }
                else if (Local != null)
                {
                    PlayerPieces Cp = isPiece(Local);
                    if (Cp != null && Cp.isMatches(CurrentPiece))
                    {
                        if (Cp.type == 3 && Cp.State == 2)
                        {
                            //ObliqueLayerChange
                            oblayer +=1;
                            if (oblayer > 1)
                            {
                                oblayer = 0;
                            }

                            Toast.makeText(getContext(),"Move as " + (oblayer == 1 ? "Unstacked" : "Stacked")+ " Piece",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (Cp != null )
                    {
                        if (CurrentPiece.type == 3 && CurrentPiece.State != 2 && Cp.type == 3 && Cp.State != 2)
                        {
                            //ObliqueMove
                            MoveObPlayer(Local,Cp);
                        }
                        else {
                            Selected = Local;
                            CurrentPiece = Cp;
                            oblayer = 0;
                            if (Cp.type == 3)
                            {
                                Toast.makeText(getContext(),"Move as " + (oblayer == 1 ? "Unstacked" : "Stacked")+ " Piece",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else if (isadjacent(Local))
                    {
                        //Move
                        MovePlayer(Local);
                    }
                    else
                    {
                        Selected = null;
                        CurrentPiece = null;
                        oblayer = 0;
                    }
                }
                else
                {
                    if (MinusR.grid.contains((int)event.getX(),(int)event.getY()) && CurrentPiece != null && CurrentPiece.type != 3)
                    {
                        RotatePlayer(-1);
                    }
                    else if (PlusR.grid.contains((int)event.getX(),(int)event.getY()) && CurrentPiece != null && CurrentPiece.type != 3)
                    {
                        RotatePlayer(+1);
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                //Selected = null;
                invalidate();

        }
        return super.onTouchEvent(event);
    }

    void RotatePlayer(int amount)
    {
        if (CurrentPlayer == 1)
        {
            int index = P1.indexOf(CurrentPiece);
            CurrentPiece.rotate(amount);
            P1.set(index,CurrentPiece);
        }
        else
        {
            int index = P2.indexOf(CurrentPiece);
            CurrentPiece.rotate(amount);
            P2.set(index,CurrentPiece);
        }
        ChangePlayer();
    }

    void MoveObPlayer(Position Local,PlayerPieces Cp)
    {
        if (CurrentPlayer == 1)
        {
            int index = P1.indexOf(CurrentPiece);
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            CurrentPiece.State = 2;
            P1.set(index,CurrentPiece);
            P1.remove(Cp);
        }
        else
        {
            int index = P2.indexOf(CurrentPiece);
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            CurrentPiece.State = 2;
            P2.set(index,CurrentPiece);
            P2.remove(Cp);
        }
        ChangePlayer();
    }

    void MovePlayer(Position Local)
    {
        if (CurrentPlayer == 1)
        {
            int index = P1.indexOf(CurrentPiece);
            //forOb
            if (CurrentPiece.type == 3 && CurrentPiece.State == 2 && oblayer == 1)
            {
                PlayerPieces A = new PlayerPieces(CurrentPiece.type,CurrentPiece.lx,CurrentPiece.ly,CurrentPiece.dir,1);
                P1.add(A);
                CurrentPiece.State = 1;
            }
            //Normal
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            P1.set(index,CurrentPiece);
        }
        else
        {
            int index = P2.indexOf(CurrentPiece);
            //forOb
            if (CurrentPiece.type == 3 && CurrentPiece.State == 2 && oblayer == 1)
            {
                PlayerPieces A = new PlayerPieces(CurrentPiece.type,CurrentPiece.lx,CurrentPiece.ly,CurrentPiece.dir,1);
                P2.add(A);
                CurrentPiece.State = 1;
            }
            //Normal
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            P2.set(index,CurrentPiece);
        }
        ChangePlayer();
    }

    void ChangePlayer()
    {
        //Laser!!
        Laser = DrawImageLaser();
        LaserPath = new Path();
        LaserPath.moveTo(Positions.get(Laser.get(0).lx).get(Laser.get(0).ly).grid.centerX(),Positions.get(Laser.get(0).lx).get(Laser.get(0).ly).grid.centerY());

        for (int i = 1; i < Laser.size();i++)
        {
            LaserPath.lineTo(Positions.get(Laser.get(i).lx).get(Laser.get(i).ly).grid.centerX(),Positions.get(Laser.get(i).lx).get(Laser.get(i).ly).grid.centerY());
        }

        Log.d("Hello",CaseAction + " ");
        if (CaseAction == 4)
        {
            DestroyObject(Laser.get(Laser.size() - 1));
        }

        CurrentPlayer += 1;
        if (CurrentPlayer > 2)
        {
            CurrentPlayer = 1;
        }

        oblayer = 0;
        CurrentPiece = null;
        Selected = null;
        invalidate();
    }

    void DestroyObject(PlayerPieces S)
    {
        if (!(S.type == 3 && S.State == 2))
            if (P1.contains(S))
                P1.remove(S);
            else
                P2.remove(S);
        else
        {
            S.State = 1;
            if (P1.contains(S))
                P1.set(P1.indexOf(S),S);
            else
                P2.remove(S);
        }
    }

    ArrayList<PlayerPieces> DrawImageLaser()
    {
        ArrayList<PlayerPieces> Laser = new ArrayList<>();
        int curdir = 0;
        if (CurrentPlayer == 1)
        {
            Laser.add(P1.get(0));
            curdir = P1.get(0).dir + 2;
            if (curdir > 3)
            {
                curdir -= 4;
            }
        }
        else
        {
            Laser.add(P2.get(0));
            curdir = P2.get(0).dir + 2;
            if (curdir > 3)
            {
                curdir -= 4;
            }
        }


        while (curdir < 4)
        {
            PlayerPieces np = NextPiece(new Point(Laser.get(Laser.size() - 1).lx,Laser.get(Laser.size() - 1).ly),curdir);
            if (np != null)
            {
                Laser.add(np);
                curdir = np.hitObject(curdir);
            }
            else
            {
                curdir = 5;
            }
        }
        CaseAction = curdir;
        return Laser;
    }

    PlayerPieces NextPiece(Point cp, int dir)
    {
        PlayerPieces temp = null;
        for (int i = 1 ; i < P1.size(); i++)
        {
            if (cp != new Point(P1.get(i).lx,P1.get(i).ly))
            if (dir % 2 != 0 && P1.get(i).ly == cp.y)
            {
                if (dir == 3 && P1.get(i).lx > cp.x)
                {
                    if (temp != null)
                    {
                        if (temp.lx>P1.get(i).lx)
                            temp = P1.get(i);
                    }
                    else
                    {
                        temp = P1.get(i);
                    }
                }
                else if (dir == 1 && P1.get(i).lx < cp.x)
                {
                    if (temp != null)
                    {
                        if (temp.lx < P1.get(i).lx)
                            temp = P1.get(i);
                    }
                    else
                    {
                        temp = P1.get(i);
                    }
                }
            }
            else if (dir % 2 == 0 && P1.get(i).lx == cp.x)
            {
                if (dir == 0 && P1.get(i).ly > cp.y)
                {
                    if (temp != null)
                    {
                        if (temp.ly>P1.get(i).ly)
                            temp = P1.get(i);
                    }
                    else
                    {
                        temp = P1.get(i);
                    }
                }
                else if (dir == 2 && P1.get(i).ly < cp.y)
                {
                    if (temp != null)
                    {
                        if (temp.ly < P1.get(i).ly)
                            temp = P1.get(i);
                    }
                    else
                    {
                        temp = P1.get(i);
                    }
                }
            }
        }

        for (int i = 1 ; i < P2.size(); i++)
        {
            if (cp != new Point(P2.get(i).lx,P2.get(i).ly))
            if (dir % 2 != 0 && P2.get(i).ly == cp.y)
            {
                if (dir == 3 && P2.get(i).lx > cp.x)
                {
                    if (temp != null)
                    {
                        if (temp.lx>P2.get(i).lx)
                            temp = P2.get(i);
                    }
                    else
                    {
                        temp = P2.get(i);
                    }
                }
                else if (dir == 1 && P2.get(i).lx < cp.x)
                {
                    if (temp != null)
                    {
                        if (temp.lx < P2.get(i).lx)
                            temp = P2.get(i);
                    }
                    else
                    {
                        temp = P2.get(i);
                    }
                }
            }
            else if (dir % 2 == 0 && P2.get(i).lx == cp.x)
            {
                if (dir == 0 && P2.get(i).ly > cp.y)
                {
                    if (temp != null)
                    {
                        if (temp.ly>P2.get(i).ly)
                            temp = P2.get(i);
                    }
                    else
                    {
                        temp = P2.get(i);
                    }
                }
                else if (dir == 2 && P2.get(i).ly < cp.y)
                {
                    if (temp != null)
                    {
                        if (temp.ly < P2.get(i).ly)
                            temp = P2.get(i);
                    }
                    else
                    {
                        temp = P2.get(i);
                    }
                }
            }
        }
        return temp;
    }

    boolean isadjacent(Position Local)
    {
        if (CurrentPiece.type != 5)
        for (int i = -1; i < 2 ; i++)
        {
            if (i + Selected.lx >= 0 && i + Selected.lx < gridx)
                for (int j = -1;j < 2; j++)
                {
                    if (j + Selected.ly >= 0 && j + Selected.ly < gridy && !(j==0 && i == 0))
                    {
                        if (Local.ly == j + Selected.ly && Local.lx == i + Selected.lx)
                            return true;
                    }
                }
        }
        return false;
    }

    Position SearchPositions (int fx, int fy)
    {
        for (int i = 0; i < gridx; i++)
        {
            for (int j = 0; j < gridy; j++)
            {
                if (Positions.get(i).get(j).grid.contains(fx,fy))
                {
                    return Positions.get(i).get(j);
                }
            }
        }

        return null;
    }

    PlayerPieces isPiece(Position p)
    {
        if (CurrentPlayer == 1)
        {
            for (int i = 0 ; i < P1.size(); i++)
            {
                if (p.lx == P1.get(i).lx && p.ly == P1.get(i).ly)
                {
                    return P1.get(i);
                }
            }
        }
        else
        {
            for (int i = 0 ; i < P2.size(); i++)
            {
                if (p.lx == P2.get(i).lx && p.ly == P2.get(i).ly)
                {
                    return P2.get(i);
                }
            }
        }

        return null;
    }

}

class PlayerPieces
{
    int type;
    // 1 = Paraoah 2 = Djed 3 = Obelisk 4 = Pyramid 5 = Laser
    int lx;
    int ly;

    int dir;

    int State;
    // 0 = Destroyed 1 = Active 2 = Stacked

    public PlayerPieces(int type, int lx, int ly, int dir,int State) {
        this.type = type;
        this.lx = lx;
        this.ly = ly;
        this.dir = dir;
        this.State = State;
    }

    void rotate(int amount)
    {
        dir += amount;

        if (dir > 3)
        {
            dir -= 4;
        }

        if (dir < 0)
        {
            dir += 4;
        }
    }

    boolean isMatches(PlayerPieces a)
    {
        return a.dir == dir && a.ly == ly && a.lx == lx && a.State == State && a.type==type;
    }

    //5 = cannothit//4 = hit // others ray bend going direction
    //input ray coming dir
    int hitObject(int dir)
    {
        if (type == 3)
            return 4;
        else if (type == 5)
        {
            return 5;
        }
        else if (type == 1)
        {
            return (dir % 2 == this.dir % 2)?5:6;
        }
        else if (type == 2)
        {
            if (this.dir >0)
            {
                dir -= this.dir;
                if (dir > 3)
                {
                    dir -= 4;
                }

                if (dir < 0)
                {
                    dir += 4;
                }
            }

            if (dir%2 == 0)
                dir -=3;
            else
                dir += 3;

            dir += this.dir;
            if (dir > 3)
            {
                dir -= 4;
            }

            if (dir < 0)
            {
                dir += 4;
            }

            return dir;

        }
        else
        {
            if (this.dir >0)
            {
                dir -= this.dir;
                if (dir > 3)
                {
                    dir -= 4;
                }

                if (dir < 0)
                {
                    dir += 4;
                }
            }

            if (dir == 2 || dir == 1)
            {
                dir = dir == 2?3:0;
                dir += this.dir;
                if (dir > 3)
                {
                    dir -= 4;
                }

                if (dir < 0)
                {
                    dir += 4;
                }
                return dir;
            }
            else
                return 4;
        }
    }
}

class Position
{
    Rect grid;
    int lx;
    int ly;

    public Position(Rect grid, int lx, int ly) {
        this.grid = grid;
        this.lx = lx;
        this.ly = ly;
    }
}