package myplayer;

import explosiveminers.user.*;

public class UnitPlayer {
    UnitController uc;
    Direction[] directions = Direction.values();

    public void run(UnitController uc) {
        // Code to be executed only at the beginning of the unit's lifespan
        this.uc = uc; //We assign the UnitController at the beginning of the class.

        while (true) {
            if (uc.getType() == UnitType.BASE){
                if(uc.getRound() == 1) {
                    uc.writeOnSharedArray(0, uc.getLocation().x);
                    uc.writeOnSharedArray(1, uc.getLocation().y);
                    uc.writeOnSharedArray(2, 0);
                    uc.writeOnSharedArray(3, -1);
                    uc.writeOnSharedArray(4, -1);
                }
                if(uc.getRound() >= 995){
                    while(uc.canBuyVictoryPoints(1)) uc.buyVictoryPoints(1);
                }
                while(uc.canBuyVictoryPoints(1) && uc.getVictoryPoints(uc.getTeam()) < uc.getVictoryPoints(uc.getOpponent())){
                    uc.buyVictoryPoints(1);
                }

                if(uc.canAttack() && uc.getInfo().getHealth() != 100){
                    uc.attack(uc.getLocation());
                }

                UnitInfo unitats[] = uc.senseUnits();
                for(UnitInfo element : unitats){
                    if(element.getTeam() != uc.getTeam()){
                        trySpawnSoldier();
                    }
                    else{
                        if(uc.canAttack(element.getLocation()) && element.getHealth() != element.getType().maxHealth) uc.attack(element.getLocation());
                    }
                }
                int c = 0;
                if(uc.readOnSharedArray(2) == 0){
                    while(uc.getResources() >= 10) {
                        if(!trySpawnMiner()) break;
                    }
                }else{
                    while(uc.getResources() >= 100 && uc.getRound() <= 400) {
                        if(!trySpawnSoldier()) break;
                    }
                    while(uc.getRound() <= 140 && c < 4 && uc.getResources() >= 20){
                        if(!trySpawnMiner()) break;
                        c++;
                    }
                    while(uc.getRound() > 400 && uc.getRound() <= 700 && uc.getResources() >= 20){
                        if(!trySpawnMiner()) break;
                    }
                    if(uc.getRound() > 400 && uc.getRound() <= 850 && uc.getResources() >= 120) trySpawnSoldier();
                    if(uc.getRound() > 700 && uc.getRound() <= 995) trySpawnMiner();
                    while(uc.getResources() >= 250 && uc.canBuyVictoryPoints(1)) uc.buyVictoryPoints(1);
                }
            }
            if(uc.getType() == UnitType.MINER){
                int x = uc.readOnSharedArray(0);
                int y = uc.readOnSharedArray(1);
                int theirs = -10;
                UnitInfo[] unitats = uc.senseUnits();
                for(UnitInfo uni : unitats){
                    if(uni.getTeam() != uc.getTeam()) {
                        if (uni.getType() == UnitType.BASE){
                            uc.writeOnSharedArray(3, uni.getLocation().x);
                            uc.writeOnSharedArray(4, uni.getLocation().y);
                        }else if(uc.readOnSharedArray(3) == -1){
                            uc.writeOnSharedArray(3, uni.getLocation().x);
                            uc.writeOnSharedArray(4, uni.getLocation().y);
                        }
                        uc.writeOnSharedArray(2, 1);
                    }
                    if(Math.abs(uc.getLocation().x - uni.getLocation().x) + Math.abs(uc.getLocation().y - uni.getLocation().y) == 1 ||
                            (Math.abs(uc.getLocation().x - uni.getLocation().x) == 1 && Math.abs(uc.getLocation().y - uni.getLocation().y) == 1)){
                        if(uni.getTeam() != uc.getTeam()){
                            if(uni.getType() == UnitType.SOLDIER) theirs += 5 * uni.getHealth();
                            if(uni.getType() == UnitType.TOWER) theirs += 7 * uni.getHealth();
                            if(uni.getType() == UnitType.BASE){
                                uc.selfDestruct();
                            }
                            if(uni.getType() == UnitType.MINER) theirs += 10;
                        }else{
                            if(uni.getType() == UnitType.SOLDIER) theirs -= 5 * uni.getHealth();
                            if(uni.getType() == UnitType.TOWER) theirs -= 7 * uni.getHealth();
                            if(uni.getType() == UnitType.BASE) theirs -= 1000000000;
                            if(uni.getType() == UnitType.MINER) theirs -= 10;
                        }
                    }
                }
                if(theirs > 0) uc.selfDestruct();
                if(uc.senseResourceAtLocation(uc.getLocation()).amount > 0  && uc.canMine()) uc.mine();
                else {
                    if(Math.abs(x - uc.getLocation().x) + Math.abs(y - uc.getLocation().y) <= 7) move_far();
                    ResourceInfo[] res = uc.senseResources();
                    int mn = 100000;
                    Location l = null;
                    for (ResourceInfo r : res) {
                        if (l == null) l = r.getLocation();
                        else if (uc.getLocation().distanceSquared(r.getLocation()) < uc.getLocation().distanceSquared(l)) {
                            l = r.getLocation();
                        }
                    }
                    if(l != null) {
                        make_move(l);
                    }else move_far();
                }
            }
            if(uc.getType() == UnitType.SOLDIER) {
                Location l = uc.getLocation();
                int x = uc.readOnSharedArray(0);
                int y = uc.readOnSharedArray(1);
                if(l.x == uc.readOnSharedArray(3) && l.y == uc.readOnSharedArray(4)){
                    uc.writeOnSharedArray(3, -1);
                    uc.writeOnSharedArray(4, -1);
                }
                UnitInfo[] unitats = uc.senseUnits();
                int minhealth = 10000;
                Location Lloc = null;
                for (UnitInfo uni : unitats){
                    if(uni.getTeam() == uc.getTeam())continue;
                    Location loc = uni.getLocation();
                    if(uni.getType() == UnitType.BASE){
                        uc.writeOnSharedArray(3, loc.x);
                        uc.writeOnSharedArray(4, loc.y);
                        if(uc.canAttack(loc)) uc.attack(loc);
                        make_move(loc);
                    }
                    if(uni.getHealth()<minhealth && uc.canAttack(loc)){
                        minhealth = uni.getHealth();
                        Lloc = loc;
                    }
                    if(Lloc == null){
                        Lloc = loc;
                    }else if(!uc.canAttack(Lloc)){
                        if(l.distanceSquared(Lloc) > l.distanceSquared(loc)){
                            Lloc = loc;
                        }
                    }
                }
                if(Lloc != null) {
                    if (uc.canAttack(Lloc)) uc.attack(Lloc);
                    make_move(Lloc);
                }
                if(uc.readOnSharedArray(3) != -1){
                    if(uc.getRound() < 150) {
                        if(Math.abs(x - l.x) + Math.abs(y - l.y) >= 7 &&
                                Math.abs(x - l.x) + Math.abs(y - l.y) <= 15) moveRandom();
                        else if(Math.abs(x - l.x) + Math.abs(y - l.y) < 7){
                            move_far();
                        }else{
                            make_move(new Location(x, y));
                        }
                    }else if(uc.getRound() < 400){
                        if(uc.readOnSharedArray(3) != -1) make_move(new Location(uc.readOnSharedArray(3), uc.readOnSharedArray(4)));
                        else move_far();
                    }else if(uc.getRound() <= 550){
                        if(Math.abs(x - l.x) + Math.abs(y - l.y) >= 7 &&
                                Math.abs(x - l.x) + Math.abs(y - l.y) <= 15) moveRandom();
                        else if(Math.abs(x - l.x) + Math.abs(y - l.y) < 7){
                            move_far();
                        }else{
                            make_move(new Location(x, y));
                        }
                    }else{
                        if(Math.abs(x - l.x) + Math.abs(y - l.y) >= 15){
                            if((Lloc == null || !uc.canAttack(Lloc)) && uc.canTransform() && uc.getInfo().getHealth() >= 5){
                                uc.transform();
                            }
                        }
                        else if(Math.abs(x - l.x) + Math.abs(y - l.y) < 15){
                            move_far();
                        }else if(Math.abs(x - l.x) + Math.abs(y - l.y) > 18){
                            make_move(new Location(x, y));
                        }
                    }
                }else if(Lloc != null){
                    uc.writeOnSharedArray(3, Lloc.x);
                    uc.writeOnSharedArray(4, Lloc.y);
                }
            }
            if(uc.getType() == UnitType.TOWER) {
                UnitInfo[] unitats = uc.senseUnits();
                int minhealth = 10000;
                Location Lloc = null;
                for (UnitInfo uni : unitats){
                    if(uni.getTeam() == uc.getTeam())continue;
                    if(uni.getHealth()<minhealth && uc.canAttack(uni.getLocation())){
                        minhealth = uni.getHealth();
                        Lloc = uni.getLocation();
                    }
                }
                if(Lloc != null){
                    if(uc.canAttack(Lloc)) {
                        uc.attack(Lloc);
                    }
                }
            }
            uc.yield(); // End of turn
        }
    }

    boolean trySpawnSoldier(){
        if(uc.getResources() < 50) return false;
        for (Direction direction : directions){
            if (uc.canSpawn(UnitType.SOLDIER, direction)){
                uc.spawn(UnitType.SOLDIER, direction);
                return true;
            }
        }
        return false;
    }

    boolean trySpawnMiner(){
        for (Direction direction : directions){
            if (uc.canSpawn(UnitType.MINER, direction)){
                uc.spawn(UnitType.MINER, direction);
                return true;
            }
        }
        return false;
    }

    void moveRandom(){
        if(!uc.canMove()) return;
        int r = (int)(Math.random()*8);
        while (!uc.canMove(directions[r])){
            r = (int) (Math.random()*8);
        }
        uc.move(directions[r]);
    }

    final int INF = 1000000;

    boolean rotateRight = true; //if I should rotate right or left
    Location lastObstacleFound = null; //latest obstacle I've found in my way
    int minDistToEnemy = INF; //minimum distance I've been to the enemy while going around an obstacle
    Location prevTarget = null; //previous target

    void resetPathfinding(){
        lastObstacleFound = null;
        minDistToEnemy = INF;
    }
    void make_move(Location target){
        if(!uc.canMove()) return;
        if (target == null) return;

        //different target? ==> previous data does not help!
        if (prevTarget == null || !target.isEqual(prevTarget)) resetPathfinding();

        //If I'm at a minimum distance to the target, I'm free!
        Location myLoc = uc.getLocation();
        int d = myLoc.distanceSquared(target);
        if (d <= minDistToEnemy) resetPathfinding();

        //Update data
        prevTarget = target;
        minDistToEnemy = Math.min(d, minDistToEnemy);

        //If there's an obstacle I try to go around it [until I'm free] instead of going to the target directly
        Direction dir = myLoc.directionTo(target);
        if (lastObstacleFound != null) dir = myLoc.directionTo(lastObstacleFound);

        //This should not happen for a single unit, but whatever
        if (uc.canMove(dir)) resetPathfinding();

        //I rotate clockwise or counterclockwise (depends on 'rotateRight'). If I try to go out of the map I change the orientation
        //Note that we have to try at most 16 times since we can switch orientation in the middle of the loop. (It can be done more efficiently)
        for (int i = 0; i < 16; ++i){
            if (uc.canMove(dir)){
                uc.move(dir);
                return;
            }
            Location newLoc = myLoc.add(dir);
            if (uc.isOutOfMap(newLoc)) rotateRight = !rotateRight;
                //If I could not go in that direction and it was not outside of the map, then this is the latest obstacle found
            else lastObstacleFound = myLoc.add(dir);
            if (rotateRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }

        if (uc.canMove(dir)) uc.move(dir);
    }

    void move_far(){
        if(!uc.canMove()) return;
        Direction dir = uc.getLocation().directionTo(new Location(uc.readOnSharedArray(0), uc.readOnSharedArray(1)));
        dir = dir.opposite();
        if(uc.canMove(dir)) uc.move(dir);
        dir = dir.rotateRight();
        if(uc.canMove(dir)) uc.move(dir);
        dir = dir.rotateLeft();
        dir = dir.rotateLeft();
        if(uc.canMove(dir)) uc.move(dir);
        if(uc.canMove()) moveRandom();
    }
}