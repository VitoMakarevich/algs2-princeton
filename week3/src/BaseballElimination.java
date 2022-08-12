import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaseballElimination {
  // Schedule starts from that offset in internal stats representation
  private final static int SCHEDULE_OFFSET = 4;

  private int maxWins = 0;
  // Representation is [id, wins, loses, remaining, ...schedule]
  private final Map<String, int[]> statsByTeam = new HashMap<>();
  private final Map<Integer, String> idToTeam = new HashMap<>();
  private final Map<String, Iterable<String>> certsCache = new HashMap<>();
  private final Map<String, Boolean> eliminatedCache = new HashMap<>();
  private final Map<String, Integer> matchesIfTeamEliminated = new HashMap<>();
  
  // create a baseball division from given filename in format specified below
  public BaseballElimination(String filename) {
    In in = new In(filename);
    // skip number of teams since we use maps, useful if array used as data holder
    in.readLine();
    int i = 0;
    while (!in.isEmpty()) {
      String line = in.readLine();
      String[] lineSplitted = line.trim().split("\\s+");
      String nameOfTheTeam = lineSplitted[0];
      this.idToTeam.put(i, nameOfTheTeam);
      int[] stats = IntStream.concat(IntStream.of(i++), Arrays.stream(lineSplitted).skip(1)
                                                              .mapToInt(Integer::parseInt))
                             .toArray();
      statsByTeam.put(nameOfTheTeam, stats);
    }
    for (String team : teams()) {
      if (maxWins < wins(team)) {
        maxWins = wins(team);
      }
    }
  }

  public int numberOfTeams() {
    return this.statsByTeam.size();
  }

  public Iterable<String> teams() {
    return this.statsByTeam.keySet();
  }

  public int wins(String team) {
    checkTeamValid(team);
    return this.statsByTeam.get(team)[1];
  }

  // Internal method operating team ids
  private int wins(int team) {
    return this.statsByTeam.get(this.idToTeam.get(team))[1];
  }

  public int losses(String team) {
    checkTeamValid(team);
    return this.statsByTeam.get(team)[2];
  }

  public int remaining(String team) {
    checkTeamValid(team);
    return this.statsByTeam.get(team)[3];
  }

  // Team name to id
  private int teamId(String team) {
    return this.statsByTeam.get(team)[0];
  }

  public int against(String team1, String team2) {
    checkTeamValid(team1);
    checkTeamValid(team2);
    int team2Id = teamId(team2);
    // Positionally remaining matches starts after team id, wins, losses, remaining (4th item is matches against team with id 0).
    return this.statsByTeam.get(team1)[SCHEDULE_OFFSET + team2Id];
  }

  // Internal method operating team ids
  private int against(int team1, int team2) {
    // Positionally remaining matches starts after team id, wins, losses, remaining (4th item is matches against team with id 0).
    return this.statsByTeam.get(this.idToTeam.get(team1))[SCHEDULE_OFFSET + team2];
  }

  public boolean isEliminated(String teamToCheck) {
    checkTeamValid(teamToCheck);
    if (!this.eliminatedCache.containsKey(teamToCheck)) {
      this.internalEliminated(teamToCheck);
    }

    return this.eliminatedCache.get(teamToCheck);
  }

  // Internal team id to name of the team
  private String idToTeam(int id) {
    return this.idToTeam.get(id);
  }

  // Calculates is team eliminated and certificate and puts data to cache
  private void internalEliminated(String teamToCheck) {
    int theoreticalWinsForTeamToCheck = wins(teamToCheck) + remaining(teamToCheck);
    if (theoreticalWinsForTeamToCheck < maxWins) {
      this.eliminatedCache.put(teamToCheck, Boolean.TRUE);
      this.certsCache.put(teamToCheck, this.statsByTeam.keySet().stream().filter(
          e -> wins(e) > theoreticalWinsForTeamToCheck).collect(
          Collectors.toList()));
      return;
    }
    int elimTeam = teamId(teamToCheck);
    this.matchesIfTeamEliminated.put(teamToCheck, 0);
    FlowNetwork flowNetwork = createGraphForCheckTeam(this.teamId(teamToCheck),
                                                      this.wins(teamToCheck) + this.remaining(
                                                          teamToCheck));
    FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);
    boolean hasChance = true;
    Set<String> cert = new HashSet<>();
    for (FlowEdge e : flowNetwork.adj(0)) {
      hasChance = (e.flow() == e.capacity()) && hasChance;
      if (fordFulkerson.inCut(e.to())) {
        Set<String> thisCut = this.matchIdToTeams(e.to(), elimTeam).stream().map(this::idToTeam)
                                  .collect(
                                      Collectors.toSet());
        cert.addAll(thisCut);
      }
    }
    boolean eliminated = !hasChance;
    this.eliminatedCache.put(teamToCheck, eliminated);
    this.certsCache.put(teamToCheck, eliminated ? cert : null);
  }

  // Creates graph for checking if single team eliminated
  private FlowNetwork createGraphForCheckTeam(int teamToCheck,
                                              int totalPossibleWinsForTeamToCheck) {
    int numberOfVerticles = calculateNumberOfVerticlesIfOneTeamEliminated();
    FlowNetwork flowNetwork = new FlowNetwork(numberOfVerticles);
    // Add matches between teams
    for (int i = 0; i < numberOfTeams(); i++) {
      if (i == teamToCheck) continue;
      int teamIGraphId = findTeamIdInGraph(i, teamToCheck);
      for (int j = i + 1; j < numberOfTeams(); j++) {
        if (j == teamToCheck) continue;
        int matchId = findMatchId(i, j, teamToCheck);
        int matches = against(i, j);
        // add connection from start to match with remaining matches as max flow
        this.addEdge(flowNetwork, 0, matchId, matches);
        this.matchesIfTeamEliminated.put(idToTeam(teamToCheck),
                                         this.matchesIfTeamEliminated.get(idToTeam(teamToCheck))
                                             + matches);
        // add connection from match to each team in this match
        this.addEdge(flowNetwork, matchId, teamIGraphId, Integer.MAX_VALUE);
        int teamJGraphId = findTeamIdInGraph(j, teamToCheck);
        this.addEdge(flowNetwork, matchId, teamJGraphId, Integer.MAX_VALUE);
      }
      // add connection to the end
      this.addEdge(flowNetwork, teamIGraphId, numberOfVerticles - 1,
                   totalPossibleWinsForTeamToCheck - wins(i));
    }

    return flowNetwork;
  }

  // Every edge addition goes through that method for debugging purposes
  private void addEdge(FlowNetwork flowNetwork, int from, int to, double weight) {
    flowNetwork.addEdge(new FlowEdge(from, to, weight));
  }

  // Maps team IDs to match id between them
  private int findMatchId(int team1, int team2, int elimTeamId) {
    int teamLeft = Math.min(team1, team2);
    int teamRight = Math.max(team1, team2);

    int id = 0;
    int teamsWithout1 = numberOfTeams() - 2;
    if (elimTeamId < teamLeft) teamLeft--;
    if (elimTeamId < teamRight) teamRight--;
    for (int i = 0; i < teamLeft; i++) {
      id += teamsWithout1 - i;
    }

    id += teamRight - teamLeft;

    return id;
  }

  // Calculates number of unique combinations of teams - 1
  private int numOfUniqueMatchesIfOneTeamEliminated() {
    // It's arithmetic progression sum up from 1 to matches with step 1.
    return (numberOfTeams() - 2) * (numberOfTeams() - 1) / 2;
  }

  // Calculates vertex that represents single team result in maxflow
  private int findTeamIdInGraph(int teamId, int elimTeam) {
    // 0 idx is start, then each unique team combination(match) starting from 1, then first team id
    int num = 1 + numOfUniqueMatchesIfOneTeamEliminated() + teamId;
    return teamId > elimTeam ? num - 1 : num;
  }

  // Calculates number of vertexes in graph
  private int calculateNumberOfVerticlesIfOneTeamEliminated() {
    int numOfTeams = numberOfTeams() - 1;
    // start + unique combinations(matches) + each team except one that we check + end
    return 2 + numOfUniqueMatchesIfOneTeamEliminated() + numOfTeams;
  }

  // Checks is team known
  private void checkTeamValid(String team) {
    if (!this.statsByTeam.containsKey(team)) {
      throw new IllegalArgumentException("Team is invalid");
    }
  }

  // Knowing the graph structure and eliminated command calculates vertex that represents match between them
  private List<Integer> matchIdToTeams(int id, int elimId) {
    int teams = numberOfTeams() - 2;
    int left = 0;
    while (true) {
      if (id - (teams - left) > 0) {
        id -= teams - left++;
      }
      else {
        List<Integer> res = new LinkedList<>();
        int right = left + id;
        if (elimId <= left) {
          left++;
          right++;
        }
        else if (elimId <= right) {
          right++;
        }
        res.add(left);
        res.add(right);

        return res;
      }
    }
  }

  public Iterable<String> certificateOfElimination(String team) {
    checkTeamValid(team);
    if (!this.eliminatedCache.containsKey(team)) {
      internalEliminated(team);
    }
    return this.certsCache.get(team);
  } // subset R of teams that eliminates given team; null if not eliminated
}
