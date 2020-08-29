package com.vssve.khet;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

    int BoardColor,SelectedColor;

    Paint BoardPaint,SelectedPaint;

    ArrayList<ArrayList<Position>> Positions;

    private Position Selected;


    public KHETBoard(Context context, AttributeSet attr) {
        super(context,attr);


        BoardColor = Color.GRAY;
        SelectedColor = Color.YELLOW;

        padding = 5 *(int) getResources().getDisplayMetrics().density;

        Positions = new ArrayList<>();

        BoardPaint = new Paint();
        BoardPaint.setColor(BoardColor);
        BoardPaint.setStyle(Paint.Style.FILL);

        SelectedPaint = new Paint();
        SelectedPaint.setColor(SelectedColor);
        SelectedPaint.setStyle(Paint.Style.FILL);

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
                PlayerPieces  A = new PlayerPieces(P1.get(j).type,P1.get(j).lx,P1.get(j).ly,P1.get(i).dir,P1.get(i).State);
                A.lx = gridx - 1 - A.lx;
                A.ly = gridy - 1 - A.ly;
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
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 1 + "pp" + P1.get(i).type, "drawable",getContext().getPackageName())),null, Positions.get(P1.get(i).lx).get(P1.get(i).ly).grid,null);
        }
        for(int i = 0; i < P2.size();i++)
        {
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("p" + 2 + "pp" + P2.get(i).type, "drawable",getContext().getPackageName())),null, Positions.get(P2.get(i).lx).get(P2.get(i).ly).grid,null);
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
                    }
                    else
                    {
                        Selected = null;
                    }
                }
                else if (Local != null)
                {
                    PlayerPieces Cp = isPiece(Local);
                    if (Cp != null && Cp.isMatches(CurrentPiece))
                    {
                        if (Cp.type == 3)
                        {
                            //ObliqueFun
                        }
                    }
                    else if (Cp != null )
                    {
                        Selected = Local;
                        CurrentPiece = Cp;
                    }
                    else if (isadjacent(Local))
                    {
                        //Move
                        MovePlayer(Local);

                    }
                    else
                    {
                        Selected = null;
                    }
                }
                else
                {
                    Selected = null;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                //Selected = null;
                invalidate();

        }
        return super.onTouchEvent(event);
    }

    void MovePlayer(Position Local)
    {
        if (CurrentPlayer == 1)
        {
            int index = P1.indexOf(CurrentPiece);
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            P1.set(index,CurrentPiece);
        }
        else
        {
            int index = P2.indexOf(CurrentPiece);
            CurrentPiece.lx = Local.lx;
            CurrentPiece.ly = Local.ly;
            P2.set(index,CurrentPiece);
        }
        ChangePlayer();
    }

    void ChangePlayer()
    {
        CurrentPlayer += 1;
        if (CurrentPlayer > 2)
        {
            CurrentPlayer = 1;
        }

        //Laser!!


        CurrentPiece = null;
        Selected = null;
        invalidate();
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